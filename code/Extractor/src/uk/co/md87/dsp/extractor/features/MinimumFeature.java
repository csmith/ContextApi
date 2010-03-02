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
public class MinimumFeature extends SingleSeriesFeature {

    public MinimumFeature(int series) {
        super(series);
    }

    @Override
    protected String getFeatureName() {
        return "Minimum";
    }

    @Override
    protected float getValue(float[] values) {
        float min = Float.MAX_VALUE;

        for (float value : values) {
            min = Math.min(min, value);
        }

        return min;
    }

}
