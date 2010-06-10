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

package uk.co.md87.android.contextanalyser.locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

import uk.co.md87.android.contextapi.ContextApi.Activities;
import uk.co.md87.android.contextapi.ContextApi.Intents;
import uk.co.md87.android.contextapi.ContextApi.Predictions;

/**
 * Receives broadcast intents from Locale and the Context Analyser.
 *
 * @author chris
 */
public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (com.twofortyfouram.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction())) {
            final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.Intent.EXTRA_BUNDLE);

            if (bundle == null) {
                return;
            }

            if (bundle.containsKey("activity")) {
                final Cursor cursor = context.getContentResolver().query(
                        Activities.CONTENT_URI,
                        new String[] { Activities.ColumnNames.ACTIVITY }, null, null, null);

                if (cursor.moveToFirst()) {
                    final String activity = cursor.getString(
                            cursor.getColumnIndex(Activities.ColumnNames.ACTIVITY));

                    if (activity.equals(bundle.getString("activity"))) {
                        setResultCode(com.twofortyfouram.Intent.RESULT_CONDITION_SATISFIED);
                    } else {
                        setResultCode(com.twofortyfouram.Intent.RESULT_CONDITION_UNSATISFIED);
                    }
                } else {
                    setResultCode(com.twofortyfouram.Intent.RESULT_CONDITION_UNKNOWN);
                }
            } else if (bundle.containsKey("destination")) {
                final Cursor cursor = context.getContentResolver().query(
                        Predictions.CONTENT_URI,
                        new String[] { Predictions.ColumnNames.PLACE,
                        Predictions.ColumnNames.COUNT}, null, null, null);
                
                final int placeColumn = cursor.getColumnIndex(Predictions.ColumnNames.PLACE);
                final int countColumn = cursor.getColumnIndex(Predictions.ColumnNames.COUNT);
                final Map<Integer, Integer> predictions = new HashMap<Integer, Integer>();

                int max = 0, total = 0;
                if (cursor.moveToFirst()) {
                    do {
                        int value = cursor.getInt(countColumn);
                        predictions.put(cursor.getInt(placeColumn), value);
                        max = Math.max(max, value);
                        total += value;
                    } while (cursor.moveToNext());
                }

                if (predictions.containsKey(bundle.getInt("destination"))
                        && predictions.get(bundle.getInt("destination")) == max) {
                    setResultCode(com.twofortyfouram.Intent.RESULT_CONDITION_SATISFIED);
                } else {
                    setResultCode(com.twofortyfouram.Intent.RESULT_CONDITION_UNSATISFIED);
                }
            }
        } else if (Intents.ACTIVITY_CHANGED.equals(intent.getAction())) {
            final Intent broadcast = new Intent(com.twofortyfouram.Intent.ACTION_REQUEST_QUERY);
            broadcast.putExtra(com.twofortyfouram.Intent.EXTRA_ACTIVITY,
                    EditActivity.class.getName());
            context.sendBroadcast(intent);
        } else if (Intents.CONTEXT_CHANGED.equals(intent.getAction())) {
            final Intent broadcast = new Intent(com.twofortyfouram.Intent.ACTION_REQUEST_QUERY);
            broadcast.putExtra(com.twofortyfouram.Intent.EXTRA_ACTIVITY,
                    EditDestination.class.getName());
            context.sendBroadcast(intent);
        }
    }

}
