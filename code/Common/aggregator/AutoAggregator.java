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

import uk.co.md87.android.common.Classifier;
import uk.co.md87.android.common.accel.AccelReader;
import uk.co.md87.android.common.accel.Sampler;

/**
 * An {@link Aggregator} which automatically samples data from an
 * {@link AccelReader} using a {@link Sampler}, and then classifies it using
 * a {@link Classifier}.
 * 
 * @author chris
 */
public class AutoAggregator extends Aggregator implements Runnable {
    
    protected final Sampler sampler;
    protected final Classifier classifier;
    protected final Runnable callback;

    public AutoAggregator(final Context context, final Handler handler,
            final AccelReader reader, final Set<Entry<Float[], String>> model,
            final Runnable callback) {
        this.sampler = new Sampler(handler, reader, this);
        this.classifier = new Classifier(model);
        this.callback = callback;
    }

    /** {@inheritDoc} */
    public void run() {
        addClassification(classifier.classify(sampler.getData()));
        callback.run();
    }

    public void stop() {
        sampler.stop();
    }

    public void start() {
        sampler.start();
    }

}
