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
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author chris
 */
public class SensorLoggerService extends Service {

    private static final String TAG = "SensorLoggerService";

    public static boolean STARTED = false;

    private SensorManager manager;
    private FileOutputStream stream;
    private OutputStreamWriter writer;

    private Timer timer;

    private volatile int i = 0;
    private float[] accelValues = new float[3],
            magValues = new float[3], orientationValues = new float[3];

    private final SensorEventListener accelListener = new SensorEventListener() {

        /** {@inheritDoc} */
        @Override
        public void onSensorChanged(final SensorEvent event) {
            setAccelValues(event.values);
        }

        /** {@inheritDoc} */
        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
            // Don't really care
        }

    };

    private final SensorEventListener magneticListener = new SensorEventListener() {

        /** {@inheritDoc} */
        @Override
        public void onSensorChanged(final SensorEvent event) {
            setMagValues(event.values);
        }

        /** {@inheritDoc} */
        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
            // Don't really care
        }

    };

    private final SensorEventListener orientationListener = new SensorEventListener() {

        /** {@inheritDoc} */
        @Override
        public void onSensorChanged(final SensorEvent event) {
            setOrientationValues(event.values);
        }

        /** {@inheritDoc} */
        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
            // Don't really care
        }

    };

    public void setAccelValues(float[] accelValues) {
        this.accelValues = accelValues;
    }

    public void setMagValues(float[] magValues) {
        this.magValues = magValues;
    }

    public void setOrientationValues(float[] orientationValues) {
        this.orientationValues = orientationValues;
    }

    public void write() {
        try {
            writer.write(System.currentTimeMillis() + ":" +
                    accelValues[SensorManager.DATA_X] + "," +
                    accelValues[SensorManager.DATA_Y] + "," +
                    accelValues[SensorManager.DATA_Z] + "," +
                    magValues[SensorManager.DATA_X] + "," +
                    magValues[SensorManager.DATA_Y] + "," +
                    magValues[SensorManager.DATA_Z] + "," +
                    orientationValues[SensorManager.DATA_X] + "," +
                    orientationValues[SensorManager.DATA_Y] + "," +
                    orientationValues[SensorManager.DATA_Z] + "," + "\n");

            if (++i % 50 == 0) {
                writer.flush();

                if (i % 1000 == 0) {
                    upload();
                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "Unable to write", ex);
        }
    }

    public void upload() {
        stopSelf();
        startService(new Intent(SensorLoggerService.this,
                UploaderService.class));
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new Binder();
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        super.onStart(intent, startId);

        STARTED = true;

        init();
    }

    public void init() {
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
        manager.registerListener(magneticListener,
                manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        manager.registerListener(orientationListener,
                manager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);

        timer = new Timer("Data logger");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                write();
            }
        }, 10000, 50);
    }

    @Override
    public void onDestroy() {
        manager.unregisterListener(accelListener);

        timer.cancel();

        STARTED = false;

        Toast.makeText(getApplicationContext(), "Sensor logger service stopped",
                Toast.LENGTH_SHORT).show();
    }

}
