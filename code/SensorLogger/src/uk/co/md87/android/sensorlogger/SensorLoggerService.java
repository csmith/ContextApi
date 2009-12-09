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
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author chris
 */
public class SensorLoggerService extends Service {

    private static final String TAG = "SensorLoggerService";

    private SensorManager manager;
    private FileOutputStream stream;
    private OutputStreamWriter writer;

    private final SensorEventListener accelListener = new SensorEventListener() {

        private int i = 0;

        /** {@inheritDoc} */
        @Override
        public void onSensorChanged(final SensorEvent event) {
            try {
                writer.write(System.currentTimeMillis() + ":" +
                        event.values[SensorManager.DATA_X] + "," +
                        event.values[SensorManager.DATA_Y] + "," +
                        event.values[SensorManager.DATA_Z] + "\n");

                if (++i == 10) {
                    writer.flush();

                    if (++i == 1000) {
                        // Auto upload!
                        
                        stopSelf();
                        startService(new Intent(SensorLoggerService.this,
                                UploaderService.class));
                    }
                }
            } catch (IOException ex) {

            }
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

        try {
            stream = openFileOutput("sensors.log", MODE_APPEND | MODE_WORLD_READABLE);
            writer = new OutputStreamWriter(stream);
        } catch (FileNotFoundException ex) {
            return;
        }
        
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        manager.registerListener(accelListener,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        Toast.makeText(getApplicationContext(), "Sensor logger service started",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        manager.unregisterListener(accelListener);

        Toast.makeText(getApplicationContext(), "Sensor logger service stopped",
                Toast.LENGTH_SHORT).show();
    }

}
