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

package uk.co.md87.android.contextanalyser;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.co.md87.android.contextanalyser.model.Journey;
import uk.co.md87.android.contextanalyser.model.JourneyStep;
import uk.co.md87.android.contextanalyser.model.Place;

/**
 * Facilitates accessing the SQLite database used for storing places and
 * journeys.
 *
 * @author chris
 */
public class DataHelper {

    public static final String LOCATIONS_TABLE = "locations";
    public static final String JOURNEYS_TABLE = "journeys";
    public static final String JOURNEYSTEPS_TABLE = "journeysteps";

    private static final String DATABASE_NAME = "contextapi.db";
    private static final int DATABASE_VERSION = 4;

    private static final String INSERT_LOCATION = "insert into "
      + LOCATIONS_TABLE + "(name, lat, lon) values (?, ?, ?)";
    private static final String INSERT_JOURNEY = "insert into "
      + JOURNEYS_TABLE + "(start, end, steps) values (?, ?, ?)";
    private static final String INSERT_JOURNEYSTEP = "insert into "
      + JOURNEYSTEPS_TABLE + "(activity, reptitions, journey, next) values (?, ?, ?, ?)";
    private static final String UPDATE_LOCATION = "update "
      + LOCATIONS_TABLE + " set name = ? where _id = ?";
    private static final String UNNAMED_QUERY = "name LIKE '%.%,%.%'";
    private static final String LOCATION_QUERY = "lat > %1$s - 0.005 and "
            + "lat < %1$s + 0.005 and lon > %2$s - 0.01 and lon < %2$s + 0.01";
    private static final String JOURNEY_STEPS_QUERY = "journey = %1$s";
    private static final String JOURNEY_START_QUERY = "start = %1$s";
    private static final String JOURNEY_BOTH_QUERY = JOURNEY_START_QUERY + " AND end = %1$s";

    private final SQLiteStatement insertLocationStatement, insertJourneyStatement,
            insertJourneyStepStatement, updateLocationStatement;

    private SQLiteDatabase db;

    public DataHelper(final Context context) {
        final OpenHelper helper = new OpenHelper(context);
        this.db = helper.getWritableDatabase();
        this.insertLocationStatement = db.compileStatement(INSERT_LOCATION);
        this.updateLocationStatement = db.compileStatement(UPDATE_LOCATION);
        this.insertJourneyStatement = db.compileStatement(INSERT_JOURNEY);
        this.insertJourneyStepStatement = db.compileStatement(INSERT_JOURNEYSTEP);
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }

    public long addLocation(final String name, final double lat, final double lon) {
        Log.i(getClass().getSimpleName(), "Adding new place at " + lat + ", " + lon);
        insertLocationStatement.bindString(1, name);
        insertLocationStatement.bindDouble(2, lat);
        insertLocationStatement.bindDouble(3, lon);
        return insertLocationStatement.executeInsert();
    }

    public void updateLocation(final long id, final String name) {
        Log.i(getClass().getSimpleName(), "Setting name of place " + id + " to " + name);
        updateLocationStatement.bindString(1, name);
        updateLocationStatement.bindLong(2, id);
        updateLocationStatement.execute();
    }

    public Map<String, Long> getUnnamedLocations() {
        final Map<String, Long> results = new HashMap<String, Long>();
        
        final Cursor cursor = db.query(LOCATIONS_TABLE,
                new String[] { Place._ID, Place.NAME },
                UNNAMED_QUERY, null, null, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                results.put(cursor.getString(1), cursor.getLong(0));
            } while (cursor.moveToNext());
        }

        closeCursor(cursor);

        return results;
    }

    public Collection<Journey> findJourneys(final Place start) {
        return findJourneys(start, null);
    }

    public Collection<Journey> findJourneys(final Place start,
            final Place end) {
        final Collection<Journey> results = new LinkedList<Journey>();

        final String query;
        if (end == null) {
            query = String.format(JOURNEY_START_QUERY, start.getId());
        } else {
            query = String.format(JOURNEY_BOTH_QUERY, start.getId(), end.getId());
        }

        final Cursor cursor = db.query(JOURNEYS_TABLE,
                new String[] { "_id", "start", "end", "steps" },
                query, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                results.add(new Journey(cursor.getLong(0), cursor.getLong(1),
                        cursor.getLong(2), cursor.getInt(3)));
            } while (cursor.moveToNext());
        }

        closeCursor(cursor);

        return results;
    }

    public void addJourney(final Place start, final Place end, final List<String> activities) {
        final List<JourneyStep> steps = getSteps(activities);
        final Collection<Journey> journeys = findJourneys(start, end);

        for (Journey journey : journeys) {
            if (journey.getSteps() == steps.size()) {
                final List<JourneyStep> theirSteps = getSteps(journey);

                if (theirSteps.equals(steps)) {
                    // TODO: Increment journey count/time/etc
                    return;
                }
            }
        }

        insertJourneyStatement.bindLong(1, start.getId());
        insertJourneyStatement.bindLong(2, end.getId());
        insertJourneyStatement.bindLong(3, steps.size());
        final long id = insertJourneyStatement.executeInsert();

        long next = 0;
        for (int i = steps.size() - 1; i >= 0; i--) {
            final JourneyStep step = steps.get(i);

            insertJourneyStepStatement.bindString(1, step.getActivity());
            insertJourneyStepStatement.bindLong(2, step.getRepetitions());
            insertJourneyStepStatement.bindLong(3, id);
            insertJourneyStepStatement.bindLong(4, next);
            next = insertJourneyStepStatement.executeInsert();
        }
    }

    protected static List<JourneyStep> getSteps(final List<String> activities) {
        final List<JourneyStep> steps = new LinkedList<JourneyStep>();

        String last = null;
        int count = 0;

        for (String activity : activities) {
            if (activity.equals(last)) {
                count++;
            } else {
                if (last != null) {
                    steps.add(new JourneyStep(last, count));
                }

                count = 1;
                last = activity;
            }
        }

        steps.add(new JourneyStep(last, count));

        return steps;
    }

    public List<JourneyStep> getSteps(final Journey journey) {
        final Map<Long, JourneyStep> results
                = new HashMap<Long, JourneyStep>(journey.getSteps());

        final String query = String.format(JOURNEY_STEPS_QUERY, journey.getId());

        final Cursor cursor = db.query(JOURNEYSTEPS_TABLE,
                new String[] { "_id", "activity", "repetitions", "next" },
                query, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                results.put(cursor.getLong(3),
                        new JourneyStep(cursor.getLong(0), cursor.getString(1),
                        cursor.getInt(2), journey.getId(), cursor.getLong(3)));
            } while (cursor.moveToNext());
        }

        closeCursor(cursor);

        final List<JourneyStep> ordered = new LinkedList<JourneyStep>();

        long previous = 0;
        while (results.containsKey(previous)) {
            final JourneyStep step = results.get(previous);
            ordered.add(step);
            previous = step.getId();
        }

        return ordered;
    }

    public Place findLocation(final double lat, final double lon) {
        final Cursor cursor = db.query(LOCATIONS_TABLE,
                new String[] { Place._ID, Place.NAME, Place.LATITUDE, Place.LONGITUDE },
                String.format(LOCATION_QUERY, lat, lon), null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                final float[] res = new float[1];
                Location.distanceBetween(lat, lon, cursor.getDouble(2), cursor.getDouble(3), res);

                if (res[0] <= 500) {
                    final Place place = new Place(cursor.getLong(0), cursor.getString(1),
                            cursor.getDouble(2), cursor.getDouble(3));

                    closeCursor(cursor);
                    
                    return place;
                }
            } while (cursor.moveToNext());
        }

        closeCursor(cursor);

        return null;
    }

    private static void closeCursor(final Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(final Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /** {@inheritDoc} */
        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + LOCATIONS_TABLE
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, lon REAL, lat REAL)");
            db.execSQL("CREATE TABLE " + JOURNEYS_TABLE
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, start INTEGER,"
                    + " end INTEGER, steps INTEGER)");
            db.execSQL("CREATE TABLE " + JOURNEYSTEPS_TABLE
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, activity TEXT,"
                    + " repetitions INTEGER, journey INTEGER, next INTEGER)");
        }

        /** {@inheritDoc} */
        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                final int newVersion) {
            Log.i(getClass().getSimpleName(), "Upgrading DB " + oldVersion + "->" + newVersion);
            if (oldVersion <= 2) {
                db.execSQL("DROP TABLE " + LOCATIONS_TABLE);
                onCreate(db);
            } else if (oldVersion <= 3) {
                db.execSQL("DROP TABLE " + LOCATIONS_TABLE);
                db.execSQL("DROP TABLE " + JOURNEYS_TABLE);
                db.execSQL("DROP TABLE " + JOURNEYSTEPS_TABLE);
                onCreate(db);
            }
        }

    }

}
