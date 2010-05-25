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

package uk.co.md87.android.common.geo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * A {@link LocationMonitor} implementation that uses the device's coarse
 * (network) location provider as a data source.
 *
 * @author chris
 */
public class RealLocationMonitor implements LocationMonitor {

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

    private final LocationManager manager;
    private float accuracy;
    private double lon, lat;

    public RealLocationMonitor(final Context context) {
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        
        manager.removeUpdates(listener);
    }

}
