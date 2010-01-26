/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    static final String VERSION = "0.1.1";

    static String ACTIVITY = "Unknown";

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        ((Button) findViewById(R.id.start)).setOnClickListener(this);
        ((Button) findViewById(R.id.upload)).setOnClickListener(this);


        ((TextView) findViewById(R.id.text)).setText("Welcome to sensor logger v"
                + VERSION + "..."
                + "\n\nThis application records any changes in your phone's "
                + "accelerometer and magnetic field sensors to a text file, "
                + "along with the timestamp that the change occured at.\n\n"
                + "Once 1,000 entries have been recorded (~50 seconds), the data will be "
                + "automatically uploaded and erased from the device. You can "
                + "manually trigger an upload using the button below.\n\n"
                + "Once you press the start button, there will be a 10 second "
                + "delay for you to put the phone in your pocket etc before monitoring "
                + "begins.\n\n");
        ((TextView) findViewById(R.id.caption)).setText("Activity name:");
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
