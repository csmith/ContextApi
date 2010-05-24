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
            duration = "<1 min";
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
        String name = "activity" + (getClassification().length() == 0
                ? "_unknown" : getClassification().substring(10)
                .replace("/", "_").toLowerCase());

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
