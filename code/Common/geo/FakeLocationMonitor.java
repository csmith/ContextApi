/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.common.geo;

/**
 *
 * @author chris
 */
public class FakeLocationMonitor implements LocationMonitor {

    public float getAccuracy() {
        return 0f;
    }

    public double getLat() {
        return 51.481386d;
    }

    public double getLon() {
        return -0.084667d;
    }

}
