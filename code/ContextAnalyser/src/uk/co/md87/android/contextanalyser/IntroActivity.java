/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.contextanalyser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import uk.co.md87.android.common.geo.LocationMonitor;

/**
 *
 * @author chris
 */
public class IntroActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here

        final LocationMonitor loc = new LocationMonitor(this);
        Log.i("IntroActivity 1", String.valueOf(loc.getLat()));
        Log.i("IntroActivity 2", String.valueOf(loc.getLon()));
        Log.i("IntroActivity 3", String.valueOf(loc.getAccuracy()));
    }

}
