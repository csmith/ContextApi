/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.activityrecorder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;
import uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder;

/**
 *
 * @author chris
 */
public class ClassifierService extends Service implements Runnable {

    ActivityRecorderBinder service = null;
    String classification;

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = ActivityRecorderBinder.Stub.asInterface(arg1);

            try {
                service.submitClassification(classification);
            } catch (RemoteException ex) {

            }
            
            stopSelf();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(ClassifierService.this, R.string.error_disconnected, Toast.LENGTH_LONG);
        }
    };

    private float[] data;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        //Log.i(getClass().getName(), "Starting classifier");

        data = intent.getFloatArrayExtra("data");

        new Thread(this).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService(connection);
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

        classification = bestActivity;

        //Log.i(getClass().getName(), "Classification: " + classification);

        bindService(new Intent(this, RecorderService.class), connection, BIND_AUTO_CREATE);
    }

}
