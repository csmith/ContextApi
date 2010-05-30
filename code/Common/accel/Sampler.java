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

package uk.co.md87.android.common.accel;

import android.os.Handler;

/**
 * A utility class which handles sampling accelerometer data from an
 * {@link AccelReader}. The Sampler takes 128 samples with a 50ms delay between
 * each sample. When the Sampler has finished, it executes a runnable so that
 * the data may be retrieved and analysed.
 *
 * @author chris
 */
public class Sampler implements Runnable {

    private final Handler handler;
    private final AccelReader reader;
    private final Runnable finishedRunnable;

    private final float[] data = new float[6];
    private int nextSample;

    public Sampler(final Handler handler, final AccelReader reader,
            final Runnable finishedRunnable) {
        this.handler = handler;
        this.reader = reader;
        this.finishedRunnable = finishedRunnable;
    }

    public void start() {
        data[0] = Float.MAX_VALUE;
        data[1] = Float.MIN_VALUE;
        data[2] = 0;
        data[3] = Float.MAX_VALUE;
        data[4] = Float.MIN_VALUE;
        data[5] = 0;
        nextSample = 0;

        reader.startSampling();

        handler.postDelayed(this, 50);
    }

    public float[] getData() {
        return data;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        final float[] values = reader.getSample();

        data[0] = Math.min(data[0], values[0]);
        data[1] = Math.max(data[1], values[0]);
        data[2] += values[0];

        data[3] = Math.min(data[3], values[1]);
        data[4] = Math.max(data[4], values[1]);
        data[5] += values[1];

        if (++nextSample == 128) {
            reader.stopSampling();
            finishedRunnable.run();
            return;
        }

        handler.postDelayed(this, 50);
    }

    public void stop() {
        handler.removeCallbacks(this);
    }

}
