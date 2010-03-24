/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.activityrecorder.rpc;

import uk.co.md87.android.activityrecorder.rpc.Classification;

/**
 *
 * @author chris
 */
interface ActivityRecorderBinder {

    boolean isRunning();

    void submitClassification(String classification);

    List<Classification> getClassifications();

}
