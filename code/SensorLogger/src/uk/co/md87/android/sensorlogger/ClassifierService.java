/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import java.util.Map;

/**
 *
 * @author chris
 */
public class ClassifierService extends Service implements Runnable {

    private float[] data;
    private static int i = 0;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        data = intent.getFloatArrayExtra("data");

        new Thread(this, "Sensor logger classifier thread").start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void run() {
        float oddTotal = 0, evenTotal = 0;
        float oddMin = Float.MAX_VALUE, oddMax = Float.MIN_VALUE;
        float evenMin = Float.MAX_VALUE, evenMax = Float.MIN_VALUE;

        for (int i = 0; i < 128; i++) {
            evenTotal += data[i * 2];
            oddTotal += data[i * 2 + 1];

            evenMin = Math.min(evenMin, data[i * 2]);
            oddMin = Math.min(oddMin, data[i * 2 + 1]);

            evenMax = Math.max(evenMax, data[i * 2]);
            oddMax = Math.max(oddMax, data[i * 2 + 1]);
        }

        final float[] points = {
            Math.abs(evenTotal / 128),
            Math.abs(oddTotal / 128),
            evenMax - evenMin,
            oddMax - oddMin
        };

        float bestDistance = Float.MAX_VALUE;
        String bestActivity = "UNCLASSIFIED/UNKNOWN";
        
        for (Map.Entry<Float[], String> entry : SensorLoggerService.model.entrySet()) {
            float distance = 0;

            for (int i = 0; i < points.length; i++) {
                distance += Math.pow(points[i] - entry.getKey()[i], 2);
            }

            if (distance < bestDistance) {
                bestDistance = distance;
                bestActivity = entry.getValue();
            }
        }

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

        int icon = R.drawable.icon;
        CharSequence tickerText = "Classification complete";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        Context context = getApplicationContext();
        CharSequence contentTitle = "Classified sample #" + ++i + " as...";
        CharSequence contentText = bestActivity;
        Intent notificationIntent = new Intent(this, ClassifierService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        mNotificationManager.notify(i, notification);

        stopSelf();
    }

}
