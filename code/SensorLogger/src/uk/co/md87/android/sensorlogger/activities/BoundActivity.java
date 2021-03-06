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

package uk.co.md87.android.sensorlogger.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import uk.co.md87.android.sensorlogger.R;
import uk.co.md87.android.sensorlogger.SensorLoggerService;
import uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder;

/**
 *
 * @author chris
 */
public class BoundActivity extends Activity {

    SensorLoggerBinder service = null;

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = SensorLoggerBinder.Stub.asInterface(arg1);
            serviceBound();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(BoundActivity.this, R.string.error_disconnected, Toast.LENGTH_LONG);
            setResult(RESULT_CANCELED);
            finish();
        }
    };

    protected void serviceBound() {
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the service as otherwise it'll stop as soon as our activities
        // unbind from it.
        startService(new Intent(this, SensorLoggerService.class));

        bindService(new Intent(this, SensorLoggerService.class), connection,
                BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(connection);
    }


}
