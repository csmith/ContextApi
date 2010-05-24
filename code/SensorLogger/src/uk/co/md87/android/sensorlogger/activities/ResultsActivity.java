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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.util.TimerTask;

import uk.co.md87.android.sensorlogger.R;

/**
 *
 * @author chris
 */
public class ResultsActivity extends BoundActivity {

    private final Handler handler = new Handler();
    private AutoCompleteTextView input;
    private ProgressDialog dialog;

    private final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkStage();
            }
    };

    private final OnClickListener yesListener = new OnClickListener() {

        public void onClick(View arg0) {
            FlurryAgent.onEvent("results_yes_click");

            findViewById(R.id.resultsno).setEnabled(false);
            findViewById(R.id.resultsyes).setEnabled(false);

            dialog = ProgressDialog.show(ResultsActivity.this, "Please wait",
                    "Submitting...", true);
            try {
                service.submit();
            } catch (RemoteException ex) {
                Log.e(getClass().getName(), "Unable to submit correction", ex);
            }
        }
    };

    private final DialogInterface.OnClickListener correctionListener
            = new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface arg0, int arg1) {
            FlurryAgent.onEvent("results_correction_submitted");

            dialog = ProgressDialog.show(ResultsActivity.this, "Please wait",
                    "Submitting...", true);
            
            try {
                service.submitWithCorrection(input.getText().toString());
            } catch (RemoteException ex) {
                Log.e(getClass().getName(), "Unable to submit correction", ex);
            }
        }
        
    };

    private final OnClickListener noListener = new OnClickListener() {

        public void onClick(View arg0) {
            FlurryAgent.onEvent("results_no_click");

            findViewById(R.id.resultsno).setEnabled(false);
            findViewById(R.id.resultsyes).setEnabled(false);

            input = new AutoCompleteTextView(ResultsActivity.this);
            input.setAdapter(new ArrayAdapter<String>(ResultsActivity.this,
                    android.R.layout.simple_dropdown_item_1line, new String[] {
                "Walking", "Walking (up stairs)", "Walking (down stairs)",
                "On a bus", "In a car", "Standing", "Sitting", "Dancing"
            }));
            input.setSingleLine();
            input.setThreshold(0);

            AlertDialog.Builder adb = new AlertDialog.Builder(ResultsActivity.this);
            adb.setView(input).setCancelable(false);
            adb.setTitle(R.string.correct_title);
            adb.setMessage(R.string.correct_activity);
            adb.setPositiveButton(R.string.correct_button, correctionListener);
            adb.create().show();
        }
    };

    @Override
    protected void serviceBound() {
        super.serviceBound();

        try {
            String name = "activity_" + service.getClassification().substring(11)
                    .replace("/", "_").toLowerCase();

            int res = getResources().getIdentifier(name, "string", "uk.co.md87.android.sensorlogger");
            ((TextView) findViewById(R.id.resultsresult)).setText(res);
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Unable to get classification", ex);
        }

        handler.postDelayed(task, 500);
    }

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.results);

        ((Button) findViewById(R.id.resultsyes)).setOnClickListener(yesListener);
        ((Button) findViewById(R.id.resultsno)).setOnClickListener(noListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void checkStage() {
        try {
            if (service.getState() == 7) {
                FlurryAgent.onEvent("results_to_thanks");

                service.setState(8);
                startActivity(new Intent(this, ThanksActivity.class));
                finish();
            } else {
                handler.postDelayed(task, 500);
            }
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Unable to get state", ex);
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
