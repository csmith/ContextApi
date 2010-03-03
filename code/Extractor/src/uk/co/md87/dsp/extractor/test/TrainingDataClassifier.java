/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
                        continue;
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
                        continue;
                    }

                    String bestActivity = c.classify(window);

                    if (bestActivity.equals(activity)) {
                        right++;
                    } else {
                        System.out.println("Wrong: " + activity + " classified as " + bestActivity);
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
