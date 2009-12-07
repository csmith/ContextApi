/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import java.util.Arrays;

/**
 *
 * @author chris
 */
public class SensorLoggerService extends Service {

    private static final String TAG = "SensorLoggerService";

    private SensorManager manager;

    private final SensorEventListener accelListener = new SensorEventListener() {

        /** {@inheritDoc} */
        @Override
        public void onSensorChanged(final SensorEvent event) {
            Log.i(TAG, event.sensor.getName() + ": " + Arrays.toString(event.values));
        }

        /** {@inheritDoc} */
        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
            // Don't really care
        }

    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        super.onStart(intent, startId);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        for (Sensor sensor : manager.getSensorList(SensorManager.SENSOR_ACCELEROMETER)) {
            manager.registerListener(accelListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        Log.i(TAG, "Registered listeners!");
    }

}
