/*
 * Copyright (c) 2009-2010 Chris Smith
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
import uk.co.md87.android.common.ModelReader;
import uk.co.md87.android.common.accel.AccelReader;
import uk.co.md87.android.common.accel.AccelReaderFactory;
import uk.co.md87.android.common.accel.Sampler;
import uk.co.md87.android.common.aggregator.Aggregator;

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

    private final Runnable registerRunnable = new Runnable() {

        public void run() {
            //Log.i(getClass().getName(), "Registering");
            sampler.start();
            
            handler.postDelayed(registerRunnable, 30000);
        }

    };

    private final Runnable analyseRunnable = new Runnable() {

        public void run() {
            final Intent intent = new Intent(RecorderService.this, ClassifierService.class);
            intent.putExtra("data", sampler.getData());
            startService(intent);
        }

    };

    final Handler handler = new Handler();

    boolean running;
    final Aggregator aggregator = new Aggregator();
    AccelReader reader;
    Sampler sampler;
    public static Map<Float[], String> model;
    private final List<Classification> classifications = new ArrayList<Classification>();

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        super.onStart(intent, startId);

        running = true;

        reader = new AccelReaderFactory().getReader(this);
        sampler = new Sampler(handler, reader, analyseRunnable);

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

            if (sampler != null) {
                sampler.stop();
            }
            
            handler.removeCallbacks(registerRunnable);
        }
    }

}
