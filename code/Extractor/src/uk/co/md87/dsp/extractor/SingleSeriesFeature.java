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

package uk.co.md87.dsp.extractor;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author chris
 */
public abstract class SingleSeriesFeature implements Feature {
    
    private final int series;

    public SingleSeriesFeature(final int series) {
        this.series = series;
    }

    protected abstract String getFeatureName();

    public String getName() {
        return getFeatureName() + " (series " + series + ")";
    }

    public float getValue(final Window window) {
        final float[] values = new float[window.getData().size()];
        int i = 0;

        for (float[] set : window.getData()) {
            if (set.length <= series) {
                return Float.NaN;
            }

            values[i++] = set[series];
        }

        return getValue(values);
    }

    protected abstract float getValue(final float[] values);

    public static Set<Feature> createFeatures(
            final Class<? extends SingleSeriesFeature> type, final int number) {
        final Set<Feature> res = new HashSet<Feature>(number);

        for (int i = 0; i < number; i++) {
            try {
                res.add((Feature) type.getConstructor(Integer.TYPE).newInstance(i));
            } catch (Exception ex) {
                // Don't really care
            }
        }

        return res;
    }

}
