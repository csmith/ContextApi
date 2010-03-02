/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
