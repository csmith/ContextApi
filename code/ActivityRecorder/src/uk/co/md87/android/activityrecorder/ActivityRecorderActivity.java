/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.activityrecorder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder;

/**
 *
 * @author chris
 */
public class ActivityRecorderActivity extends Activity {

    ActivityRecorderBinder service = null;

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = ActivityRecorderBinder.Stub.asInterface(arg1);
        }

        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(ActivityRecorderActivity.this,
                    R.string.error_disconnected, Toast.LENGTH_LONG);
        }
    };

    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);
    }

}
