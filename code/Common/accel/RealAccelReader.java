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

package uk.co.md87.android.common.accel;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;

/**
 * An accelerometer reader which reads real data from the device's
 * accelerometer.
 *
 * @author chris
 */
public class RealAccelReader implements AccelReader {

    private final SensorEventListener accelListener = new SensorEventListener() {

        /** {@inheritDoc} */
        @Override
        public void onSensorChanged(final SensorEvent event) {
            values = new float[]{
                event.values[SensorManager.DATA_Y],
                event.values[SensorManager.DATA_Z]
            };
        }

        /** {@inheritDoc} */
        @Override
        public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
            // Don't really care
        }

    };

    float[] values = new float[]{0, 0};
    private SensorManager manager;
    private PowerManager.WakeLock wl;

    public RealAccelReader(final Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Activity recorder");
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        wl.acquire();
    }

    public void startSampling() {
        manager.registerListener(accelListener,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stopSampling() {
        manager.unregisterListener(accelListener);
    }

    public float[] getSample() {
        return values;
    }

    @Override
    protected void finalize() throws Throwable {
        wl.release();
    }

}
