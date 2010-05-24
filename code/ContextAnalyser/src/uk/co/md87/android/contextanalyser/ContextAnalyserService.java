/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.contextanalyser;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import java.util.Map;
import uk.co.md87.android.common.ModelReader;

/**
 *
 * @author chris
 */
public class ContextAnalyserService extends Service {

    private final Runnable minuteRunnable = new Runnable() {

        public void run() {
            poll();
        }
    };

    public static Map<Float[], String> model;

    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        model = ModelReader.getModel(this, R.raw.basic_model);
        
        handler.postDelayed(minuteRunnable, 60000);
    }
    
    public void poll() {
        handler.postDelayed(minuteRunnable, 60000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(minuteRunnable);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
