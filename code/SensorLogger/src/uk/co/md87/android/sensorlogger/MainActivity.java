/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 *
 * @author chris
 */
public class MainActivity extends Activity implements OnClickListener {

    static final String VERSION = "0.1.6";

    static String ACTIVITY = "Unknown";

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        ((Button) findViewById(R.id.start)).setOnClickListener(this);
        ((Button) findViewById(R.id.upload)).setOnClickListener(this);
        ((Button) findViewById(R.id.upload)).setEnabled(false);

        ((TextView) findViewById(R.id.text)).setText("This application records any changes in your phone's "
                + "accelerometer and magnetic field sensors to a text file, "
                + "along with the timestamp that the change occured at.\n\n"
                + "Once 1,000 entries have been recorded (~50 seconds), the data will be "
                + "automatically uploaded and erased from the device. You can "
                + "manually trigger an upload using the button below.\n\n"
                + "Once you press the start button, there will be a 10 second "
                + "delay for you to put the phone in your pocket etc before monitoring "
                + "begins.");
        ((TextView) findViewById(R.id.caption)).setText("Activity name:");

        final String imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        final String code = "http://MD87.co.uk/android/p/" + getCode(imei);
        ((TextView) findViewById(R.id.viewcaption)).setText("View your submitted data online at:\n " + code);
        Linkify.addLinks(((TextView) findViewById(R.id.viewcaption)), Linkify.WEB_URLS);
    }

    public String getCode(final String imei) {
        final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-=_";
        final StringBuilder builder = new StringBuilder();

        long val = Long.decode(imei == null ? "0"
                : imei.matches("^[0-9]+$") ? imei : ("0x" + imei));

        while (val > 0) {
            final long bit = val % chars.length();
            val = val / chars.length();
            builder.insert(0, chars.charAt((int) bit));
        }

        while (builder.length() < 10) {
            builder.insert(0, "a");
        }

        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(final View view) {
        ACTIVITY = ((EditText) findViewById(R.id.entry)).getText().toString();
        
        if (view.getId() == R.id.start) {
            if (!SensorLoggerService.STARTED) {
                startService(new Intent(this, SensorLoggerService.class));
            }
        } else if (view.getId() == R.id.upload) {
            stopService(new Intent(this, SensorLoggerService.class));
            startService(new Intent(this, UploaderService.class));
        }
    }

}
