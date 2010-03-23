/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.activityrecorder.rpc;

/**
 *
 * @author chris
 */
interface ActivityRecorderBinder {

    boolean isRunning();

    void submitClassification(String classification);

    Map getClassifications();

}
