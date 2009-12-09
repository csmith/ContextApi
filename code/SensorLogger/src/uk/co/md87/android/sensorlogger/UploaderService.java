/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author chris
 */
public class UploaderService extends Service implements Runnable {

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        new Thread(this, "Sensor logger upload thread").start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void run() {
        final HttpPost post = new HttpPost("http://chris.smith.name/android/upload");
        final File file = getFileStreamPath("sensors.log");
        final FileEntity entity = new FileEntity(file, "text/plain");

        post.setEntity(entity);
        post.addHeader("x-application", "SensorLogger");
        post.addHeader("x-version", MainActivity.VERSION);
        post.addHeader("x-imei", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());

        try {
            int code = new DefaultHttpClient().execute(post).getStatusLine().getStatusCode();

            file.delete();
        } catch (IOException ex) {
            Log.e("UploaderService", "Unable to upload sensor logs", ex);
        }

        startService(new Intent(this, SensorLoggerService.class));
        stopSelf();
    }

}
