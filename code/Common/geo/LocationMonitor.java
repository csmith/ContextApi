/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.common.geo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 *
 * @author chris
 */
public class LocationMonitor {

    private final LocationListener listener = new LocationListener() {

        public void onLocationChanged(Location arg0) {
            handleLocation(arg0);
        }

        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // Don't really care
        }

        public void onProviderEnabled(String arg0) {
            // Don't really care
        }

        public void onProviderDisabled(String arg0) {
            // Don't really care
        }
    };

    private float accuracy;
    private double lon, lat;

    public LocationMonitor(final Context context) {
        final LocationManager manager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 90000, 500, listener);
        handleLocation(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    final void handleLocation(final Location location) {
        if (location == null) {
            return;
        }
        
        accuracy = location.getAccuracy();
        lon = location.getLongitude();
        lat = location.getLatitude();
    }

    public float getAccuracy() {
        return accuracy;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

}
