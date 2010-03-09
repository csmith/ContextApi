/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import uk.co.md87.android.sensorlogger.R;

/**
 *
 * @author chris
 */
public class IntroActivity extends BoundActivity implements OnClickListener {

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.intro);

        ((Button) findViewById(R.id.introstart)).setOnClickListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(final View arg0) {
        try {
            service.setState(1);
            startActivity(new Intent(this, CountdownActivity.class));
            finish();
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Error setting state", ex);
        }
    }

}
