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

package uk.co.md87.android.contextanalyser;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.Map;

import uk.co.md87.android.common.Aggregator;
import uk.co.md87.android.common.Classifier;
import uk.co.md87.android.common.ModelReader;
import uk.co.md87.android.common.accel.AccelReaderFactory;
import uk.co.md87.android.common.accel.Sampler;
import uk.co.md87.android.common.geo.LocationMonitor;
import uk.co.md87.android.common.geo.LocationMonitorFactory;

/**
 *
 * @author chris
 */
public class ContextAnalyserService extends Service {

    private final Runnable scheduleRunnable = new Runnable() {

        public void run() {
            poll();
        }
    };

    private final Runnable analyseRunnable = new Runnable() {

        public void run() {
            analyse();
        }
    };

    public static Map<Float[], String> model;

    private Sampler sampler;
    private Classifier classifier;
    private Aggregator aggregator;
    private LocationMonitor locationMonitor;
    private DataHelper dataHelper;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        locationMonitor = new LocationMonitorFactory().getMonitor(this);
        sampler = new Sampler(handler, new AccelReaderFactory().getReader(this), analyseRunnable);
        classifier = new Classifier(ModelReader.getModel(this, R.raw.basic_model).entrySet());
        aggregator = new Aggregator();
        dataHelper = new DataHelper(this);

        handler.postDelayed(scheduleRunnable, 60000);
    }
    
    public void poll() {
        handler.postDelayed(scheduleRunnable, 60000);

        sampler.start();
    }

    public void analyse() {
        aggregator.addClassification(classifier.classify(sampler.getData()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(scheduleRunnable);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
