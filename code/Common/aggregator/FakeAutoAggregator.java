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
package uk.co.md87.android.common.aggregator;

import android.content.Context;
import android.os.Handler;

import java.util.Map.Entry;
import java.util.Set;

import uk.co.md87.android.common.accel.AccelReader;

/**
 * An extension of {@link AutoAggregator} which always provides pre-determined,
 * fake classifications.
 * 
 * @author chris
 */
public class FakeAutoAggregator extends AutoAggregator {

    private static final String[] CLASSIFICATIONS = new String[] {
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/WALKING",
        "CLASSIFIED/WALKING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/SITTING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/WALKING",
        "CLASSIFIED/WALKING",
        "CLASSIFIED/WALKING",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/WALKING",
        "CLASSIFIED/WALKING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/SITTING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/WALKING",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/VEHICLE/BUS",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
        "CLASSIFIED/IDLE/STANDING",
    };

    private int i = 0;

    public FakeAutoAggregator(final Context context, final Handler handler,
            final AccelReader reader, final Set<Entry<Float[], String>> model,
            final Runnable callback) {
        super(context, handler, reader, model, callback);
    }

    @Override
    public String getClassification() {
        return CLASSIFICATIONS[i % CLASSIFICATIONS.length];
    }

    @Override
    public void start() {
        i++;
        
        callback.run();
    }

    @Override
    public void stop() {
        // Do nothing
    }

}
