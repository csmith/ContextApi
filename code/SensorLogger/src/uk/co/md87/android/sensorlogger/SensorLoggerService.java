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

package uk.co.md87.android.sensorlogger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import uk.co.md87.android.sensorlogger.activities.ResultsActivity;
import uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder;

/**
 *
 * @author chris
 */
public class SensorLoggerService extends Service {

    private final SensorLoggerBinder.Stub binder = new SensorLoggerBinder.Stub() {

        public void setState(int newState) throws RemoteException {
            doSetState(newState);
        }

        public int getCountdownTime() throws RemoteException {
            return countdown;
        }

        public int getState() throws RemoteException {
            return state;
        }

        public void submitClassification(String classification) throws RemoteException {
            if (!classifications.containsKey(classification)) {
                classifications.put(classification, 0);
            }

            classCount++;
            classifications.put(classification, classifications.get(classification) + 1);

            if (classCount == 15) {
                showNotification();
                setState(5);
            }
        }

        public String getClassification() throws RemoteException {
            int best = 0;
            String bestName = "CLASSIFIED/UNKNOWN";

            for (Map.Entry<String, Integer> entry : classifications.entrySet()) {
                if (entry.getValue() > best) {
                    best = entry.getValue();
                    bestName = entry.getKey();
                }
            }

            return bestName;
        }

        public void submitWithCorrection(String newCorrection) throws RemoteException {
            correction = newCorrection;
            submit();
        }

        public void submit() throws RemoteException {
            final Intent intent = new Intent(SensorLoggerService.this, UploaderService.class);
            intent.putExtra("correction", correction);
            intent.putExtra("activity", getClassification());
            intent.putExtra("version", getVersionName());
            intent.putExtra("application", "SensorLogger");
            intent.putExtra("imei", getIMEI());
            setState(6);
            startService(intent);
        }
        
    };

    final Handler handler = new Handler();
    final TimerTask countdownTask = new TimerTask() {

        @Override
        public void run() {
            if (--countdown > 0) {
                handler.postDelayed(countdownTask, 1000);
            } else {
                doSetState(3);
            }
        }
    };

    Map<String, Integer> classifications = new HashMap<String, Integer>();
    String correction = "UNCLASSIFIED/NOTCORRECTED";
    int classCount = 0;
    int countdown = 10;
    int state = 1;

    public String getIMEI() {
        return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException ex) {
            return "Unknown";
        }
    }

    public void showNotification() {
        NotificationManager mNotificationManager
                = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int icon = R.drawable.icon;
        CharSequence tickerText = "Classification complete";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.defaults |= Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        CharSequence contentTitle = "Recording and analysis complete";
        CharSequence contentText = "Select to see results";
        Intent notificationIntent = new Intent(this, ResultsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(getApplicationContext(), contentTitle,
                contentText, contentIntent);

        mNotificationManager.notify(0, notification);
    }

    /** {@inheritDoc} */
    @Override
    public IBinder onBind(final Intent arg0) {
        return binder;
    }

    void doSetState(final int newState) {
        switch (newState) {
            case 2:
                countdown = 10;

                handler.removeCallbacks(countdownTask);
                handler.postDelayed(countdownTask, 1000);
                break;
            case 3:
                startService(new Intent(SensorLoggerService.this, RecorderService.class));
                break;
            case 8:
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(0);
                stopSelf();
                break;
        }

        state = newState;
    }

}
