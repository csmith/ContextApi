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
public class MeanFeature extends SingleSeriesFeature {

    public MeanFeature(int series) {
        super(series);
    }

    @Override
    protected String getFeatureName() {
        return "Mean";
    }

    @Override
    protected float getValue(float[] values) {
        float total = 0;

        for (float value : values) {
            total += value;
        }

        return total / values.length;
    }

}
