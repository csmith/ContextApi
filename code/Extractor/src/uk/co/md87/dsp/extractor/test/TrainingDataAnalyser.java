/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor.test;

import com.dmdirc.util.MapList;
import java.io.IOException;
import java.util.List;
import uk.co.md87.dsp.extractor.Window;
import uk.co.md87.dsp.extractor.Windower;
import uk.co.md87.dsp.extractor.io.TrainingDataImporter;

/**
 *
 * @author chris
 */
public class TrainingDataAnalyser {

    public static void main(final String ... args) throws IOException {
        final TrainingDataImporter importer = new TrainingDataImporter(args[0]);

        final MapList<String, List<float[]>> data = importer.getTrainingData();

        for (String activity : data.keySet()) {
            System.out.println(activity);

            int i = 0, j = 0;
            for (List<float[]> dataset : data.get(activity)) {
                i++;
                j = 0;

                for (Window window : new Windower(dataset).getWindows()) {
                    j++;
                    System.out.println(i + "." + j + ": " + window.getData());
                }
            }
        }
    }

}
