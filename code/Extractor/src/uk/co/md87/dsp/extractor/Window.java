/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author chris
 */
public class Window {

    private final List<float[]> data;

    public Window(List<float[]> data) {
        this.data = data;
    }

    public List<float[]> getData() {
        return data;
    }

    public Map<String, Float> getFeatures(final Set<Feature> features) {
        final Map<String, Float> results = new TreeMap<String, Float>();

        for (Feature feature : features) {
            results.put(feature.getName(), feature.getValue(this));
        }

        return results;
    }

}
