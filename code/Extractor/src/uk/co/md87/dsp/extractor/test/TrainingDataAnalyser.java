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
