/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;
import uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder;

/**
 *
 * @author chris
 */
public abstract class BoundService extends Service {

    SensorLoggerBinder service = null;

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = SensorLoggerBinder.Stub.asInterface(arg1);
            serviceBound();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(BoundService.this, R.string.error_disconnected, Toast.LENGTH_LONG);
        }
    };

    protected void serviceBound() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService(connection);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        bindService(new Intent(this, SensorLoggerService.class), connection, BIND_AUTO_CREATE);
    }

}
