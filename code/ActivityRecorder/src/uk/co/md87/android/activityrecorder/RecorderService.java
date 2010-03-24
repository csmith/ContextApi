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
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder;
import uk.co.md87.android.activityrecorder.rpc.Classification;

/**
 *
 * @author chris
 */
public class RecorderService extends Service {

    private final ActivityRecorderBinder.Stub binder = new ActivityRecorderBinder.Stub() {

        public void submitClassification(String classification) throws RemoteException {
            Log.i(getClass().getName(), "Adding classification: " + classification);

            if (!classifications.isEmpty() && classification.equals(classifications
                    .get(classifications.size() - 1).getClassification())) {
                classifications.get(classifications.size() - 1).updateEnd(System.currentTimeMillis());
            } else {
                classifications.add(new Classification(classification, System.currentTimeMillis()));
            }
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
        Log.i(getClass().getName(), "Sampling");
        data[(nextSample * 2) % 256] = values[0];
        data[(nextSample * 2 + 1) % 256] = values[1];
        
        if (++nextSample % 64 == 0 && nextSample >= 128) {
            float[] cache = new float[256];
            System.arraycopy(data, 0, cache, 0, 256);
            analyse(cache);

            if (nextSample == 192) {
                unregister();
                return;
            }
        }

        handler.postDelayed(sampleRunnable, 50);
    }

    public void analyse(float[] data) {
        Log.i(getClass().getName(), "Analysing");
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
    }

    void register() {
        Log.i(getClass().getName(), "Registering");
        nextSample = 0;
        manager.registerListener(accelListener,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        handler.postDelayed(sampleRunnable, 50);
        handler.postDelayed(registerRunnable, 60000);
    }

    void unregister() {
        manager.unregisterListener(accelListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        running = false;

        handler.removeCallbacks(sampleRunnable);
        handler.removeCallbacks(registerRunnable);
        
        unregister();
    }

}
