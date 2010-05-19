/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.common.geo;

/**
 *
 * @author chris
 */
public interface LocationMonitor {

    float getAccuracy();

    double getLat();
    
    double getLon();

}
