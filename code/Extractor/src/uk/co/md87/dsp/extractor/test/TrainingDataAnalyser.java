/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor.test;

import com.dmdirc.util.MapList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import uk.co.md87.dsp.extractor.Feature;
import uk.co.md87.dsp.extractor.Window;
import uk.co.md87.dsp.extractor.Windower;
import uk.co.md87.dsp.extractor.features.*;
import uk.co.md87.dsp.extractor.io.TrainingDataImporter;
import static uk.co.md87.dsp.extractor.SingleSeriesFeature.createFeatures;

/**
 *
 * @author chris
 */
public class TrainingDataAnalyser {

    public static void main(final String ... args) throws IOException {
        final TrainingDataImporter importer = new TrainingDataImporter(args[0]);
        final String type = args.length > 1 ? args[1] : "";

        final MapList<String, List<float[]>> data = importer.getTrainingData();
        final Set<Feature> features = new HashSet<Feature>();

        features.addAll(createFeatures(MaximumFeature.class, 6));
        features.addAll(createFeatures(MinimumFeature.class, 6));
        features.addAll(createFeatures(RangeFeature.class, 6));
        features.addAll(createFeatures(MedianFeature.class, 6));
        features.addAll(createFeatures(AbsoluteMeanFeature.class, 6));
        features.addAll(createFeatures(MeanFeature.class, 6));

        System.out.println("@RELATION activity");
        System.out.println();

        final List<String> featureNames = new ArrayList<String>();

        for (Feature feature : features) {
            featureNames.add(feature.getName());
        }

        Collections.sort(featureNames);

        for (String name : featureNames) {
            System.out.println("@ATTRIBUTE \"" + name +"\" numeric");
        }

        System.out.print("@ATTRIBUTE classification {");

        boolean first = true;
        for (String activity : data.keySet()) {
            if (first) {
                first = false;
            } else {
                System.out.print(",");
            }

            System.out.print(activity);
        }

        System.out.println("}");
        System.out.println();
        System.out.println("@DATA");

        for (String activity : data.keySet()) {
            int j = 0;
            for (List<float[]> dataset : data.get(activity)) {

                for (Window window : new Windower(dataset).getWindows()) {
                    j++;

                    first = true;
                    for (Float value : window.getFeatures(features).values()) {
                        if (first) {
                            first = false;
                        } else {
                            System.out.print(",");
                        }

                        System.out.print(value);
                    }

                    System.out.print(",");
                    System.out.print(!type.equals(activity)
                            || (type.equals(activity) && j % 3 > 0) ? activity : "?");
                    System.out.println();
                }
            }
        }
    }

}
