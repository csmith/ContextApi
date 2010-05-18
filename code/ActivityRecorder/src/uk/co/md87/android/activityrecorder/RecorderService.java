/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.activityrecorder;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder;
import uk.co.md87.android.activityrecorder.rpc.Classification;
import uk.co.md87.android.common.Aggregator;
import uk.co.md87.android.common.ModelReader;
import uk.co.md87.android.common.accel.AccelReader;
import uk.co.md87.android.common.accel.AccelReaderFactory;

/**
 *
 * @author chris
 */
public class RecorderService extends Service {

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
            final float[] values = reader.getSample();

            data[nextSample * 2] = values[0];
            data[nextSample * 2 + 1] = values[1];

            if (++nextSample == 128) {
                float[] cache = new float[256];
                System.arraycopy(data, 0, cache, 0, 256);
                analyse(cache);

                reader.stopSampling();
                return;
            }

            handler.postDelayed(sampleRunnable, 50);
        }
        
    };

    private final Runnable registerRunnable = new Runnable() {

        public void run() {
            //Log.i(getClass().getName(), "Registering");
            nextSample = 0;

            reader.startSampling();

            handler.postDelayed(sampleRunnable, 50);
            handler.postDelayed(registerRunnable, 30000);
        }

    };

    final Handler handler = new Handler();

    float[] data = new float[256];
    volatile int nextSample = 0;

    boolean running;
    final Aggregator aggregator = new Aggregator();
    AccelReader reader;
    public static Map<Float[], String> model;
    private final List<Classification> classifications = new ArrayList<Classification>();

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

        running = true;

        reader = new AccelReaderFactory().getReader(this);

        init();
    }

    @SuppressWarnings("unchecked")
    public void init() {
        model = ModelReader.getModel(this, R.raw.basic_model);

        handler.postDelayed(registerRunnable, 1000);

        classifications.add(new Classification("", System.currentTimeMillis()));
    }

    void updateScores(final String classification) {
        aggregator.addClassification(classification);

        final String best = aggregator.getClassification();

        if (!classifications.isEmpty() && best.equals(classifications
                    .get(classifications.size() - 1).getClassification())) {
            classifications.get(classifications.size() - 1).updateEnd(System.currentTimeMillis());
        } else {
            classifications.add(new Classification(best, System.currentTimeMillis()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (running) {
            running = false;

            handler.removeCallbacks(sampleRunnable);
            handler.removeCallbacks(registerRunnable);

            reader.stopSampling();
        }
    }

}
