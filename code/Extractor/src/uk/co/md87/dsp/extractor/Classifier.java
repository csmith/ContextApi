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

    public String[] classify(final Window window) {
        final Float[] target = window.getFeatures(features).values().toArray(
                new Float[features.size()]);
        float best = Float.MAX_VALUE;
        String bestActivity = "UNCLASSIFIED/UNKNOWN", secondBest = bestActivity;

        for (Map.Entry<Float[], String> entry : model.entrySet()) {
            float distance = 0;

            for (int i = 0; i < target.length; i++) {
                distance += Math.pow(target[i] - entry.getKey()[i], 2);
            }

            if (distance < best) {
                best = distance;
                secondBest = bestActivity;
                bestActivity = entry.getValue();
            }
        }

        return new String[]{bestActivity,secondBest};
    }

}
