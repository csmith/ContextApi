/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor.test;

import com.dmdirc.util.MapList;
import java.io.IOException;
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

        final MapList<String, List<float[]>> data = importer.getTrainingData();
        final Set<Feature> features = new HashSet<Feature>();

        features.addAll(createFeatures(MaximumFeature.class, 6));
        features.addAll(createFeatures(MinimumFeature.class, 6));
        features.addAll(createFeatures(RangeFeature.class, 6));
        features.addAll(createFeatures(MedianFeature.class, 6));
        features.addAll(createFeatures(MeanFeature.class, 6));

        for (String activity : data.keySet()) {
            System.out.println(activity);

            int i = 0, j = 0;
            for (List<float[]> dataset : data.get(activity)) {
                i++;
                j = 0;

                for (Window window : new Windower(dataset).getWindows()) {
                    j++;
                    System.out.println(i + "." + j + ": " + window.getFeatures(features));
                }
            }
        }
    }

}
