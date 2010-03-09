/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Map;

/**
 *
 * @author chris
 */
public class ClassifierService extends BoundService implements Runnable {

    private float[] data;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        data = intent.getFloatArrayExtra("data");
    }

    @Override
    protected void serviceBound() {
        super.serviceBound();
        
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
        
        for (Map.Entry<Float[], String> entry : RecorderService.model.entrySet()) {
            float distance = 0;

            for (int i = 0; i < points.length; i++) {
                distance += Math.pow(points[i] - entry.getKey()[i], 2);
            }

            if (distance < bestDistance) {
                bestDistance = distance;
                bestActivity = entry.getValue();
            }
        }

        try {
            service.submitClassification(bestActivity);
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Error submitting classification", ex);
        }

        stopSelf();
    }

}
