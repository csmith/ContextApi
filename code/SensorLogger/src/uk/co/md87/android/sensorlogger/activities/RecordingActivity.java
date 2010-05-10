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
import com.flurry.android.FlurryAgent;
import java.util.TimerTask;

import uk.co.md87.android.sensorlogger.R;

/**
 *
 * @author chris
 */
public class RecordingActivity extends BoundActivity {

    private final Handler handler = new Handler();
    int state = 3;
    int phase = 3;

    private final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkSamples();
            }
    };

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.recording);

        handler.removeCallbacks(task);
        handler.postDelayed(task, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(task);
    }

    public void checkSamples() {
        try {
            phase = (phase + 1) % 4;

            String text = "?";
            switch (phase) {
                case 0: text = ".  "; break;
                case 1: text = " . "; break;
                case 2: text = "  ."; break;
                case 3: text = " . "; break;
            }

            ((TextView) findViewById(R.id.recordingcount)).setText(text);

            final int serviceState = service.getState();

            if (serviceState > state) {
                state = serviceState;

                if (state == 4) {
                    setTitle("Sensor Logger > Analysing");
                    ((TextView) findViewById(R.id.recordingheader)).setTag(R.string.analysingheader);
                }
            }

            if (serviceState > 4) {
                FlurryAgent.onEvent("countdown_to_results");
                startActivity(new Intent(this, ResultsActivity.class));
                finish();
            } else {
                handler.postDelayed(task, 500);
            }
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Error getting countdown", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onStart() {
        super.onStart();

        FlurryAgent.onStartSession(this, "TFBJJPQUQX3S1Q6IUHA6");
    }

    /** {@inheritDoc} */
    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

}
