/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.common.accel;

import android.content.Context;

/**
 *
 * @author chris
 */
public class AccelReaderFactory {

    public AccelReader getReader(final Context context) {
        return new RealAccelReader(context);
    }

}
