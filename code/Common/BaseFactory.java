/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.common;

import android.os.Build;

/**
 *
 * @author chris
 */
public abstract class BaseFactory {

    protected boolean shouldUseFake() {
        return "sdk".equals(Build.PRODUCT);
    }

}
