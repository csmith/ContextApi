/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.activityrecorder.rpc;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import java.util.Date;

/**
 *
 * @author chris
 */
public class Classification implements Parcelable {

    private CharSequence niceClassification;
    private String startTime;
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
        final String duration;
        final int length = (int) ((end - start) / 1000);

        if (length < 60) {
            duration = length + " secs";
        } else if (length < 60 * 60) {
            duration = (length / 60) + " mins";
        } else {
            duration = (length / (60 * 60)) + " hrs";
        }


        return niceClassification + "\n" + startTime + " for " + duration;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(classification);
        arg0.writeLong(start);
        arg0.writeLong(end);
    }

    public Classification withContext(final Context context) {
        String name = "activity_" + getClassification().substring(11)
                .replace("/", "_").toLowerCase();

        niceClassification = context.getResources().getText(
                context.getResources().getIdentifier(name, "string",
                "uk.co.md87.android.activityrecorder"));

        Date date = new Date(start);
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);

        startTime = dateFormat.format(date) + " " + timeFormat.format(date);
        
        return this;
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
