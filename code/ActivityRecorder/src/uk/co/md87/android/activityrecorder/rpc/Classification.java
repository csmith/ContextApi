/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.activityrecorder.rpc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * @author chris
 */
public class Classification implements Parcelable {

    private final String classification;
    private final long start;
    private long end;

    public Classification(final String classification, final long start) {
        this.classification = classification;
        this.start = start;
        this.end = start;
    }

    public void updateEnd(final long end) {
        this.end = end;
    }

    public int describeContents() {
        return 0;
    }

    public String getClassification() {
        return classification;
    }

    public long getEnd() {
        return end;
    }

    public long getStart() {
        return start;
    }

    @Override
    public String toString() {
        return classification + "\n" + start + " -- " + (end - start);
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(classification);
        arg0.writeLong(start);
        arg0.writeLong(end);
    }

    public static final Parcelable.Creator<Classification> CREATOR
             = new Parcelable.Creator<Classification>() {

        public Classification createFromParcel(Parcel arg0) {
            final Classification res = new Classification(arg0.readString(), arg0.readLong());
            res.updateEnd(arg0.readLong());
            return res;
        }

        public Classification[] newArray(int arg0) {
            return new Classification[arg0];
        }

    };

}
