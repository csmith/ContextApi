/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import java.util.TimerTask;

import uk.co.md87.android.sensorlogger.R;

/**
 *
 * @author chris
 */
public class CountdownActivity extends BoundActivity {

    private final Handler handler = new Handler();

    private final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateCountdown();
            }
    };

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.countdown);

        handler.removeCallbacks(task);
        handler.postDelayed(task, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(task);
    }

    public void updateCountdown() {
        try {
            final int time = service.getCountdownTime();

            ((TextView) findViewById(R.id.countdowntimer)).setText("" + time);

            if (time > 0) {
                handler.postDelayed(task, 500);
            } else {
                service.setState(3);
                startActivity(new Intent(this, RecordingActivity.class));
                finish();
            }
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Error updating countdown", ex);
        }
    }

}
