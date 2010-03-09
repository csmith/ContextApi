/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.sensorlogger.rpc;

/**
 *
 * @author chris
 */
interface SensorLoggerBinder {

    /**
     * Sets the state of the sensor binder application.
     *
     * 1 - introduction (not running)
     * 2 - countdown phase
     * 3 - collection phase
     * 4 - analysis phase
     * 5 - results
     * 6 - uploading/uploaded
     * 7 - upload complete
     * 8 - finished
     */
    void setState(int state);

    void submitClassification(String classification);

    void submit();

    void submitWithCorrection(String correction);

    String getClassification();

    int getState();

    int getCountdownTime();

}
