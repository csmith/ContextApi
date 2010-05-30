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

package uk.co.md87.android.sensorlogger.activities;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.flurry.android.FlurryAgent;

import uk.co.md87.android.common.ExceptionHandler;
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

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.intro);

        ((Button) findViewById(R.id.introstart)).setOnClickListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(final View arg0) {
        try {
            FlurryAgent.onEvent("intro_to_countdown");

            service.setState(2);
            startActivity(new Intent(this, CountdownActivity.class));
            finish();
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Error setting state", ex);
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
