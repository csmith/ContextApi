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

package uk.co.md87.android.contextapi;

import android.net.Uri;

/**
 * Provides access to constants used to access the exported API of the Context
 * Analyser.
 *
 * @author chris
 */
public class ContextApi {

    public static class Intents {
        public static final String ACTIVITY_CHANGED
                = "uk.co.md87.android.contextanalyser.ACTIVITY_CHANGED";
        public static final String CONTEXT_CHANGED
                = "uk.co.md87.android.contextanalyser.CONTEXT_CHANGED";
        public static final String PREDICTION_AVAILABLE
                = "uk.co.md87.android.contextanalyser.PREDICTION_AVAILABLE";

        public static class ContextTypes {
            public static final int PLACE = 1;
        }
    }

    public static class Places {

        public static final Uri CONTENT_URI = Uri.parse("content://uk.co.md87"
            + ".android.contextanalyser.placescontentprovider/places");
        
        public static final String CONTENT_TYPE = "vnd.contextanalyser.location";

        public static class ColumnNames {
            public static final String _ID = "_id";
            public static final String NAME = "name";
            public static final String LATITUDE = "lat";
            public static final String LONGITUDE = "lon";
            public static final String DURATION = "duration";
            public static final String VISIT_COUNT = "times";
            public static final String LAST_VISIT = "lastvisit";
        }

    }

    public static class Journeys {

        public static final Uri CONTENT_URI = Uri.parse("content://uk.co.md87"
                + ".android.contextanalyser.journeyscontentprovider/journeys");

        public static final String CONTENT_TYPE = "vnd.contextanalyser.journey";

        public static class ColumnNames {
            public static final String _ID = "_id";
            public static final String START = "start";
            public static final String END = "end";
            public static final String STEPS = "steps";
            public static final String NUMBER = "number";
        }

    }

    public static class JourneySteps {

        public static final Uri CONTENT_URI = Uri.parse("content://uk.co.md87"
                + ".android.contextanalyser.journeyscontentprovider/steps");

        public static final String CONTENT_TYPE = "vnd.contextanalyser.journeystep";

        public static class ColumnNames {
            public static final String _ID = "_id";
            public static final String ACTIVITY = "activity";
            public static final String REPETITIONS = "repetitions";
            public static final String JOURNEY = "journey";
            public static final String NEXT = "next";
        }

    }

    public static class Predictions {

        public static final Uri CONTENT_URI = Uri.parse("content://uk.co.md87"
                + ".android.contextanalyser.predictionscontentprovider/destination");

        public static final String CONTENT_TYPE = "vnd.contextanalyser.prediction";

        public static class ColumnNames {
            public static final String _ID = "_id";
            public static final String PLACE = "place";
            public static final String COUNT = "count";
        }

    }

    public static class Activities {

        public static final Uri CONTENT_URI = Uri.parse("content://uk.co.md87"
                + ".android.contextanalyser.activitiescontentprovider/current");

        public static final String CONTENT_TYPE = "vnd.contextanalyser.activity";

        public static class ColumnNames {
            public static final String ACTIVITY = "activity";
        }
    }

}
