/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import uk.co.md87.android.common.Classifier;

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
        try {
            service.submitClassification(
                    new Classifier(RecorderService.model.entrySet()).classify(data));
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Error submitting classification", ex);
        }

        stopSelf();
    }

}
