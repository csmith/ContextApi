/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor.test;

import java.io.IOException;
import uk.co.md87.dsp.extractor.io.TrainingDataImporter;

/**
 *
 * @author chris
 */
public class TrainingDataAnalyser {

    public static void main(final String ... args) throws IOException {
        final TrainingDataImporter importer = new TrainingDataImporter(args[0]);
        System.out.println(importer.getTrainingData().getMap());
    }

}
