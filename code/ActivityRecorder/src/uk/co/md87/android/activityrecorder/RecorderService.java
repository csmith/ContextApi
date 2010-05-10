/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.activityrecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder;
import uk.co.md87.android.activityrecorder.rpc.Classification;

/**
 *
 * @author chris
 */
public class RecorderService extends Service {

    static final double DELTA = 0.25;
    static final double THRESHOLD = 0.5;

    private final HashMap<String, HashMap<String, Double>> scores
            = new HashMap<String, HashMap<String, Double>>() {{
        put("", new HashMap<String, Double>() {{
            put("null", 0.5d);
            put("CLASSIFIED", 0.5d);
        }});

        put("CLASSIFIED", new HashMap<String, Double>() {{
            put("null", 0.2d);
            put("DANCING", 0.2d);
            put("WALKING", 0.2d);
            put("VEHICLE", 0.2d);
            put("IDLE", 0.2d);
        }});

        put("CLASSIFIED/WALKING", new HashMap<String, Double>() {{
            put("null", 0.5d);
            put("STAIRS", 0.5d);
        }});

        put("CLASSIFIED/VEHICLE", new HashMap<String, Double>() {{
            put("null", 0.333d);
            put("CAR", 0.333d);
            put("BUS", 0.333d);
        }});

        put("CLASSIFIED/IDLE", new HashMap<String, Double>() {{
            put("null", 0.333d);
            put("STANDING", 0.333d);
            put("SITTING", 0.333d);
        }});

        put("CLASSIFIED/WALKING/STAIRS", new HashMap<String, Double>() {{
            put("null", 0.333d);
            put("UP", 0.333d);
            put("DOWN", 0.333d);
        }});
    }};

    private final ActivityRecorderBinder.Stub binder = new ActivityRecorderBinder.Stub() {

        public void submitClassification(String classification) throws RemoteException {
            //Log.i(getClass().getName(), "Received classification: " + classification);

            updateScores(classification);
        }

        public List<Classification> getClassifications() throws RemoteException {
            return classifications;
        }

        public boolean isRunning() throws RemoteException {
            return running;
        }

    };

    private final Runnable sampleRunnable = new Runnable() {

        public void run() {
            sample();
        }
        
    };

    private final Runnable registerRunnable = new Runnable() {

        public void run() {
            register();
        }

    };

    private final Handler handler = new Handler();

    private SensorManager manager;
    private PowerManager.WakeLock wl;

    private float[] values = new float[2];

    private float[] data = new float[256];
    private volatile int nextSample = 0;

    boolean running;
    public static Map<Float[], String> model;
    private final List<Classification> classifications = new ArrayList<Classification>();

    private final SensorEventListener accelListener = new SensorEventListener() {

        /** {@inheritDoc} */
        @Override
        public void onSensorChanged(final SensorEvent event) {
            setAccelValues(event.values);
        }

        /** {@inheritDoc} */
        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
            // Don't really care
        }

    };

    public void setAccelValues(float[] accelValues) {
        this.values = new float[]{
            accelValues[SensorManager.DATA_Y],
            accelValues[SensorManager.DATA_Z]
        };
    }

    public void sample() {
        //Log.i(getClass().getName(), "Sampling");
        data[(nextSample * 2) % 256] = values[0];
        data[(nextSample * 2 + 1) % 256] = values[1];
        
        if (++nextSample % 64 == 0 && nextSample >= 128) {
            float[] cache = new float[256];
            System.arraycopy(data, 0, cache, 0, 256);
            analyse(cache);

            unregister();
            return;
        }

        handler.postDelayed(sampleRunnable, 50);
    }

    public void analyse(float[] data) {
        //Log.i(getClass().getName(), "Analysing");
        final Intent intent = new Intent(this, ClassifierService.class);
        intent.putExtra("data", data);
        startService(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        super.onStart(intent, startId);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Activity recorder");

        running = true;

        init();
    }

    @SuppressWarnings("unchecked")
    public void init() {
        InputStream is = null;
        try {
            is = getResources().openRawResource(R.raw.basic_model);
            model = (Map<Float[], String>) new ObjectInputStream(is).readObject();
        } catch (Exception ex) {
            Log.e(getClass().getName(), "Unable to load model", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    // Don't care
                }
            }
        }

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        handler.postDelayed(registerRunnable, 1000);

        classifications.add(new Classification("", System.currentTimeMillis()));

        wl.acquire();
    }

    void register() {
        //Log.i(getClass().getName(), "Registering");
        nextSample = 0;
        manager.registerListener(accelListener,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        handler.postDelayed(sampleRunnable, 50);
        handler.postDelayed(registerRunnable, 30000);
    }

    void unregister() {
        manager.unregisterListener(accelListener);
    }

    void updateScores(final String classification) {
        String path = "";

        for (String part : classification.split("/")) {
            if (!scores.containsKey(path)) {
                throw new RuntimeException("Path not found: " + path
                        + " (classification: " + classification + ")");
            }

            updateScores(scores.get(path), part);
            path = path + (path.length() == 0 ? "" : "/") + part;
        }

        if (scores.containsKey(path)) {
            // This classification has children which we're not using
            // e.g. we've received CLASSIFIED/WALKING, but we're not walking
            //      up or down stairs
            updateScores(scores.get(path), "null");
        }

        final String best = getClassification();

        if (!classifications.isEmpty() && best.equals(classifications
                    .get(classifications.size() - 1).getClassification())) {
            classifications.get(classifications.size() - 1).updateEnd(System.currentTimeMillis());
        } else {
            classifications.add(new Classification(best, System.currentTimeMillis()));
        }
    }

    String getClassification() {
        String path = "";

        do {
            final Map<String, Double> map = scores.get(path);
            double best = THRESHOLD;
            String bestPath = "null";

            for (Map.Entry<String, Double> entry : map.entrySet()) {
                if (entry.getValue() >= best) {
                    best = entry.getValue();
                    bestPath = entry.getKey();
                }
            }

            path = path + (path.length() == 0 ? "" : "/") + bestPath;
        } while (scores.containsKey(path));

        return path.replaceAll("(^CLASSIFIED)?/?null$", "");
    }

    void updateScores(final Map<String, Double> map, final String target) {
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            //Log.d(getClass().getName(), "Score for " + entry.getKey() + " was: " + entry.getValue());
            entry.setValue(entry.getValue() * (1 - DELTA));

            if (entry.getKey().equals(target)) {
                entry.setValue(entry.getValue() + DELTA);
            }
            //Log.d(getClass().getName(), "Score for " + entry.getKey() + " is now: " + entry.getValue());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (running) {
            running = false;

            handler.removeCallbacks(sampleRunnable);
            handler.removeCallbacks(registerRunnable);

            unregister();

            wl.release();
        }
    }

}
