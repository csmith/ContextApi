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

package uk.co.md87.android.activityrecorder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;

import java.util.List;

import uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder;
import uk.co.md87.android.activityrecorder.rpc.Classification;
import uk.co.md87.android.common.ExceptionHandler;

/**
 *
 * @author chris
 */
public class ActivityRecorderActivity extends Activity {

    ActivityRecorderBinder service = null;
    ProgressDialog dialog;

    final Handler handler = new Handler();

    private final ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = ActivityRecorderBinder.Stub.asInterface(arg1);

            handler.postDelayed(updateRunnable, 500);
        }

        public void onServiceDisconnected(ComponentName arg0) {
            service = null;
            handler.removeCallbacks(updateRunnable);
            ((Button) findViewById(R.id.togglebutton)).setEnabled(false);
        }
    };

    private final Runnable updateRunnable = new Runnable() {

        public void run() {
            updateButton();
            
            handler.postDelayed(updateRunnable, 500);
        }
    };

    private final Runnable startRunnable = new Runnable() {

        public void run() {
            startService(new Intent(ActivityRecorderActivity.this,
                    RecorderService.class));
            updateRunnable.run();
        }
    };

    private OnClickListener clickListener = new OnClickListener() {

        public void onClick(View arg0) {
            try {
                if (service.isRunning()) {
                    FlurryAgent.onEvent("recording_stop");
                    stopService(new Intent(ActivityRecorderActivity.this,
                            RecorderService.class));
                    unbindService(connection);
                    bindService(new Intent(ActivityRecorderActivity.this, RecorderService.class),
                            connection, BIND_AUTO_CREATE);
                } else {
                    FlurryAgent.onEvent("recording_start");
                    handler.removeCallbacks(updateRunnable);
                    ((Button) findViewById(R.id.togglebutton)).setEnabled(false);

                    dialog = ProgressDialog.show(ActivityRecorderActivity.this, "Starting service",
                        "Please wait...", true);

                    handler.postDelayed(startRunnable, 500);
                }
            } catch (RemoteException ex) {
                Log.e(getClass().getName(), "Unable to get service state", ex);
            }
        }
    };

    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        bindService(new Intent(this, RecorderService.class), connection, BIND_AUTO_CREATE);

        setContentView(R.layout.main);
        ((Button) findViewById(R.id.togglebutton)).setEnabled(false);
        ((Button) findViewById(R.id.togglebutton)).setOnClickListener(clickListener);
        ((ListView) findViewById(R.id.list)).setAdapter(
                new ArrayAdapter<Classification>(this, R.layout.item));
    }

    /** {@inheritDoc} */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(connection);
    }

    @SuppressWarnings("unchecked")
    void updateButton() {
        try {
            ((Button) findViewById(R.id.togglebutton)).setText(service.isRunning()
                    ? R.string.service_enabled : R.string.service_disabled);
            ((Button) findViewById(R.id.togglebutton)).setEnabled(true);

            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            
            final List<Classification> classifications = service.getClassifications();
            final ArrayAdapter<Classification> adapter = (ArrayAdapter<Classification>)
                    ((ListView) findViewById(R.id.list)).getAdapter();

            if (classifications.isEmpty()) {
                adapter.clear();
            } else if (!adapter.isEmpty()) {
                final Classification myLast = adapter.getItem(adapter.getCount() - 1);
                final Classification expected = classifications.get(adapter.getCount() - 1);

                if (myLast.getClassification().equals(expected.getClassification())) {
                    // Just update the end time
                    myLast.updateEnd(expected.getEnd());
                    adapter.notifyDataSetChanged();
                } else {
                    // Something's gone wrong - the entries should match
                    adapter.clear();
                }
            }

            for (int i = adapter.getCount(); i < classifications.size(); i++) {
                adapter.add(classifications.get(i).withContext(this));
            }
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Unable to get service state", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onStart() {
        super.onStart();

        FlurryAgent.onStartSession(this, "EMKSQFUWSCW51AKBL2JJ");
    }

    /** {@inheritDoc} */
    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }

}
