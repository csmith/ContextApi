/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.common.accel;

/**
 *
 * @author chris
 */
public interface AccelReader {

    void startSampling();

    void stopSampling();

    float[] getSample();

}
