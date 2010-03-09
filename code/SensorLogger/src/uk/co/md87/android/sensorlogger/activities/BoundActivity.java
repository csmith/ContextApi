/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
