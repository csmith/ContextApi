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
import android.widget.TextView;

/**
 *
 * @author chris
 */
public class MainActivity extends Activity implements OnClickListener {

    static final String VERSION = "0.1";

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        startService(new Intent(this, SensorLoggerService.class));

        setContentView(R.layout.main);

        ((Button) findViewById(R.id.start)).setOnClickListener(this);
        ((Button) findViewById(R.id.upload)).setOnClickListener(this);


        ((TextView) findViewById(R.id.text)).setText("Welcome to sensor logger v"
                + VERSION + "..."
                + "\n\nThis application records any changes in your phone's "
                + "accelerometer state to a text file, along with the timestamp "
                + "that the change occured at.\n\n"
                + "Hit the upload button to upload the data you've recorded so "
                + "it can be analysed. Thanks.\n\n");
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.start) {
            if (!stopService(new Intent(this, SensorLoggerService.class))) {
                startService(new Intent(this, SensorLoggerService.class));
            }
        } else if (view.getId() == R.id.upload) {
            stopService(new Intent(this, SensorLoggerService.class));
            startService(new Intent(this, UploaderService.class));
        }
    }

}
