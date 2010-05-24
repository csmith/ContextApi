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

package uk.co.md87.android.sensorlogger;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author chris
 */
public class UploaderService extends BoundService implements Runnable {

    private Map<String, String> headers = new HashMap<String, String>();

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        headers.clear();
        for (String key : intent.getExtras().keySet()) {
            headers.put(key, intent.getStringExtra(key));
        }
    }

    @Override
    protected void serviceBound() {
        super.serviceBound();
        
        new Thread(this, "Upload thread").start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void run() {
        final HttpPost post = new HttpPost("http://chris.smith.name/android/upload");
        final File file = getFileStreamPath("sensors.log");

        if (file.exists() && file.length() > 10) {
            // The file exists and contains a non-trivial amount of information
            final FileEntity entity = new FileEntity(file, "text/plain");

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                post.setHeader("x-" + entry.getKey(), entry.getValue());
            }

            post.setEntity(entity);

            try {
                int code = new DefaultHttpClient().execute(post).getStatusLine().getStatusCode();
            } catch (IOException ex) {
                Log.e(getClass().getName(), "Unable to upload sensor logs", ex);
            }
        }

        file.delete();

        try {
            service.setState(7);
        } catch (RemoteException ex) {
            Log.e(getClass().getName(), "Unable to update state", ex);
        }

        stopSelf();
    }

}
