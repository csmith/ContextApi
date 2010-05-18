/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.common;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 *
 * @author chris
 */
public class ModelReader {

    @SuppressWarnings("unchecked")
    public static Map<Float[], String> getModel(final Context context, final int res) {
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(res);
            return (Map<Float[], String>) new ObjectInputStream(is).readObject();
        } catch (Exception ex) {
            Log.e("ModelReader", "Unable to load model", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    // Don't care
                }
            }
        }

        return null;
    }

}
