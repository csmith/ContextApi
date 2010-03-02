/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor;

/**
 *
 * @author chris
 */
public interface Feature {

    String getName();

    float getValue(final Window window);

}
