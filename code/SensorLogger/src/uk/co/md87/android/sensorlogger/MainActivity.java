/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 *
 * @author chris
 */
public class MainActivity extends Activity {

    private SensorManager manager;

    private final SensorEventListener accelListener = new SensorEventListener() {

        /** {@inheritDoc} */
        @Override
        public void onSensorChanged(final SensorEvent event) {
            // Yay.
        }

        /** {@inheritDoc} */
        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
            // Don't really care
        }

    };

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        for (Sensor sensor : manager.getSensorList(SensorManager.SENSOR_ACCELEROMETER)) {
            manager.registerListener(accelListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        // ToDo add your GUI initialization code here
    }

}
