/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor.io;

import com.dmdirc.util.MapList;
import com.dmdirc.util.TextFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chris
 */
public class TrainingDataImporter {

    private final TextFile textFile;

    public TrainingDataImporter(final String file) {
        textFile = new TextFile(file);
    }

    public MapList<String, List<float[]>> getTrainingData() throws IOException {
        final MapList<String, List<float[]>> res = new MapList<String, List<float[]>>();

        String activity = null;
        List<float[]> data = null;

        for (String line : textFile.getLines()) {
            if (line.trim().isEmpty()) {
                continue;
            }
            
            if (line.startsWith("Activity: ")) {
                if (activity != null) {
                    res.add(activity, data);
                }

                activity = line.trim().substring(10);
                data = new ArrayList<float[]>(100);
            } else {
                final String[] parts = line.split(":")[1].split(",");
                final float[] values = new float[parts.length];

                for (int i = 0; i < parts.length; i++) {
                    values[i] = Float.parseFloat(parts[i]);
                }

                data.add(values);
            }
        }

        res.add(activity, data);

        return res;
    }

}
