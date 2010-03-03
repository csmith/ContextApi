/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor;

import com.dmdirc.util.StreamUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import uk.co.md87.dsp.extractor.features.AbsoluteMeanFeature;
import uk.co.md87.dsp.extractor.features.RangeFeature;

/**
 *
 * @author chris
 */
public class Classifier {

    private final Map<Float[], String> model;
    private final List<Feature> features = Arrays.asList(new Feature[]{
        new AbsoluteMeanFeature(1),
        new AbsoluteMeanFeature(2),
        new RangeFeature(1),
        new RangeFeature(2)
    });

    public Classifier(final String filename) throws IOException, ClassNotFoundException {
        this(new FileInputStream(filename));
    }

    public Classifier(final InputStream is) throws IOException, ClassNotFoundException {
        try {
            model = (Map<Float[], String>) new ObjectInputStream(is).readObject();
        } finally {
            StreamUtil.close(is);
        }
    }

    public String classify(final Window window) {
        final Float[] target = window.getFeatures(features).values().toArray(
                new Float[features.size()]);
        float best = Float.MAX_VALUE;
        String bestActivity = "UNCLASSIFIED/UNKNOWN";

        for (Map.Entry<Float[], String> entry : model.entrySet()) {
            float distance = 0;

            for (int i = 0; i < target.length; i++) {
                distance += Math.pow(target[i] - entry.getKey()[i], 2);
            }

            if (distance < best) {
                best = distance;
                bestActivity = entry.getValue();
            }
        }

        return bestActivity;
    }

}
