/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import java.util.Arrays;

/**
 *
 * @author chris
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        startService(new Intent(this, SensorLoggerService.class));

        setContentView(R.layout.main);

        ((TextView) findViewById(R.id.text)).setText(
                ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
    }

}
