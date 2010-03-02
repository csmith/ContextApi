/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor.features;

import uk.co.md87.dsp.extractor.SingleSeriesFeature;

/**
 *
 * @author chris
 */
public class RangeFeature extends SingleSeriesFeature {

    public RangeFeature(int series) {
        super(series);
    }

    @Override
    protected String getFeatureName() {
        return "Range";
    }

    @Override
    protected float getValue(float[] values) {
        float max = Float.MIN_VALUE, min = Float.MAX_VALUE;

        for (float value : values) {
            max = Math.max(max, value);
            min = Math.min(min, value);
        }

        return max - min;
    }

}
