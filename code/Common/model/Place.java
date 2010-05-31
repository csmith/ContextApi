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

package uk.co.md87.android.common.model;

import android.net.Uri;

/**
 * A place is a named location which has some significance for the user. Most
 * places come about when the user remains stationary for a period of time.
 * 
 * @author chris
 */
public class Place {

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String DURATION = "duration";
    public static final String VISIT_COUNT = "times";
    public static final String LAST_VISIT = "lastvisit";

    public static final Uri CONTENT_URI = Uri.parse("content://uk.co.md87"
            + ".android.contextanalyser.placescontentprovider/places");
    public static final String CONTENT_TYPE = "vnd.contextanalyser.location";

    private final long id;
    private final String name;
    private final double lat;
    private final double lon;

    public Place(final long id, final String name, final double lat, final double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Place{id=" + id + " name=" + name + " lat=" + lat + " lon=" + lon + '}';
    }
}
