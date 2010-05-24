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

package uk.co.md87.dsp.extractor.test;

import com.dmdirc.util.MapList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.md87.dsp.extractor.Classifier;
import uk.co.md87.dsp.extractor.Feature;
import uk.co.md87.dsp.extractor.Window;
import uk.co.md87.dsp.extractor.Windower;
import uk.co.md87.dsp.extractor.features.*;
import uk.co.md87.dsp.extractor.io.TrainingDataImporter;

/**
 *
 * @author chris
 */
public class TrainingDataClassifier {

    public static void main(final String ... args) throws IOException, ClassNotFoundException {
        final TrainingDataImporter importer = new TrainingDataImporter(args[0]);

        final MapList<String, List<float[]>> data = importer.getTrainingData();
        final Set<Feature> features = new HashSet<Feature>();

        features.add(new AbsoluteMeanFeature(1));
        features.add(new AbsoluteMeanFeature(2));
        features.add(new RangeFeature(1));
        features.add(new RangeFeature(2));

        System.out.println("Using 2/3 of data for training...");
        long now = System.currentTimeMillis();

        final Map<Float[], String> model = new HashMap<Float[], String>();

        for (String activity : data.keySet()) {
            int j = 0;
            for (List<float[]> dataset : data.get(activity)) {

                for (Window window : new Windower(dataset).getWindows()) {
                    if (j++ % 3 == 0) {
                        //continue;
                    }
                    
                    model.put(window.getFeatures(features).values().toArray(new Float[4]), activity);
                }
            }
        }

        now = System.currentTimeMillis() - now;

        System.out.println("Took " + now + "ms to build model...");

        final FileOutputStream os = new FileOutputStream("basic.model");
        new ObjectOutputStream(os).writeObject(model);
        os.close();

        System.out.println("Using remaining 1/3 for validation...");

        now = System.currentTimeMillis();

        final Classifier c = new Classifier("basic.model");

        int right = 0, wrong = 0;

        for (String activity : data.keySet()) {
            int j = 0;
            for (List<float[]> dataset : data.get(activity)) {

                for (Window window : new Windower(dataset).getWindows()) {
                    if (j++ % 3 != 0) {
                        //continue;
                    }

                    String[] bestActivities = c.classify(window);
                    String bestActivity = bestActivities[0];

                    if (bestActivity.equals(activity)) {
                        right++;
                    } else {
                        System.out.println("Wrong: " + activity
                                + " classified as " + bestActivity
                                + "; 2nd best: " + bestActivities[1]);
                        wrong++;
                    }
                }
            }
        }

        now = System.currentTimeMillis() - now;

        System.out.println("Took " + now + "ms to validate...");

        System.out.println("Right: " + right + ", Wrong: " + wrong);
    }

}
