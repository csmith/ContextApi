/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.common.geo;

import android.content.Context;
import uk.co.md87.android.common.BaseFactory;

/**
 *
 * @author chris
 */
public class LocationMonitorFactory extends BaseFactory {

    public LocationMonitor getMonitor(final Context context) {
        return shouldUseFake() ? new FakeLocationMonitor() : new RealLocationMonitor(context);
    }

}
