/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import uk.co.md87.dsp.extractor.SingleSeriesFeature;

/**
 *
 * @author chris
 */
public class MedianFeature extends SingleSeriesFeature {

    public MedianFeature(int series) {
        super(series);
    }

    @Override
    protected String getFeatureName() {
        return "Median";
    }

    @Override
    protected float getValue(float[] values) {
        final List<Float> newValues = new ArrayList<Float>(values.length);

        for (float value : values) {
            newValues.add(value);
        }

        Collections.sort(newValues);

        return newValues.get(newValues.size() / 2);
    }

}
