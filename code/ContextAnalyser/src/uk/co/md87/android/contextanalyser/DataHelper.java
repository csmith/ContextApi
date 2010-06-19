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

import uk.co.md87.android.common.model.Journey;
import uk.co.md87.android.common.model.JourneyStep;
import uk.co.md87.android.common.model.Place;

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
    private static final int DATABASE_VERSION = 9;

    private static final String INSERT_LOCATION = "insert into "
      + LOCATIONS_TABLE + "(name, lat, lon, duration, times, lastvisit) values (?, ?, ?, 0, 0, 0)";
    private static final String INSERT_JOURNEY = "insert into "
      + JOURNEYS_TABLE + "(start, end, steps, number) values (?, ?, ?, 1)";
    private static final String INSERT_JOURNEYSTEP = "insert into "
      + JOURNEYSTEPS_TABLE + "(activity, repetitions, journey, next) values (?, ?, ?, ?)";
    private static final String UPDATE_LOCATION = "update "
      + LOCATIONS_TABLE + " set name = ? where _id = ?";
    private static final String UPDATE_LOCATION_VISIT = "update "
      + LOCATIONS_TABLE + " set times = times + 1, duration = duration + ?, "
      + "lastvisit = ? WHERE _id = ?";
    private static final String UPDATE_JOURNEY = "update "
      + JOURNEYS_TABLE + " set number = number + 1 WHERE _id = ?";
    private static final String UNNAMED_QUERY = "name LIKE '%.%,%.%'";
    private static final String LOCATION_QUERY = "lat > %1$s - 0.005 and "
            + "lat < %1$s + 0.005 and lon > %2$s - 0.01 and lon < %2$s + 0.01";
    private static final String JOURNEY_STEPS_QUERY = "journey = %1$s";
    private static final String JOURNEY_START_QUERY = "start = %1$s";
    private static final String JOURNEY_BOTH_QUERY = JOURNEY_START_QUERY + " AND end = %2$s";

    private final SQLiteStatement insertLocationStatement, insertJourneyStatement,
            insertJourneyStepStatement, updateLocationStatement,
            updateLocationVisitStatement, updateJourneyStatement;

    private final SQLiteDatabase db;

    public DataHelper(final Context context) {
        final OpenHelper helper = new OpenHelper(context);
        this.db = helper.getWritableDatabase();
        this.insertLocationStatement = db.compileStatement(INSERT_LOCATION);
        this.updateLocationStatement = db.compileStatement(UPDATE_LOCATION);
        this.updateLocationVisitStatement = db.compileStatement(UPDATE_LOCATION_VISIT);
        this.insertJourneyStatement = db.compileStatement(INSERT_JOURNEY);
        this.insertJourneyStepStatement = db.compileStatement(INSERT_JOURNEYSTEP);
        this.updateJourneyStatement = db.compileStatement(UPDATE_JOURNEY);
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

        final Cursor cursor = db.query(JOURNEYS_TABLE, new String[] { Journey._ID,
                Journey.START, Journey.END, Journey.STEPS, Journey.NUMBER },
                query, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                results.add(new Journey(cursor.getLong(0), cursor.getLong(1),
                        cursor.getLong(2), cursor.getInt(3), cursor.getInt(4)));
            } while (cursor.moveToNext());
        }

        closeCursor(cursor);

        return results;
    }

    public void recordVisit(final Place place, final long start, final long end) {
        final long seconds = (end - start) / 1000;
        updateLocationVisitStatement.bindLong(1, seconds);
        updateLocationVisitStatement.bindLong(2, end / 1000);
        updateLocationVisitStatement.bindLong(3, place.getId());
        updateLocationVisitStatement.execute();
    }

    public void addJourney(final Place start, final Place end, final List<String> activities) {
        final List<JourneyStep> steps = JourneyUtil.getSteps(activities);
        final Collection<Journey> journeys = findJourneys(start, end);

        for (Journey journey : journeys) {
            if (journey.getSteps() == steps.size()) {
                final List<JourneyStep> theirSteps = getSteps(journey);

                if (JourneyUtil.isCompatible(steps, theirSteps)) {
                    updateJourneyStatement.bindLong(1, journey.getId());
                    updateJourneyStatement.execute();
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

    public List<JourneyStep> getSteps(final Journey journey) {
        final Map<Long, JourneyStep> results
                = new HashMap<Long, JourneyStep>(journey.getSteps());

        final String query = String.format(JOURNEY_STEPS_QUERY, journey.getId());

        final Cursor cursor = db.query(JOURNEYSTEPS_TABLE,
                new String[] { JourneyStep._ID, JourneyStep.ACTIVITY,
                JourneyStep.REPETITIONS, JourneyStep.NEXT },
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
            ordered.add(0, step);
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

    public void close() {
        db.close();
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(final Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /** {@inheritDoc} */
        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + LOCATIONS_TABLE
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, "
                    + "lon REAL, lat REAL, duration INTEGER, times INTEGER, "
                    + "lastvisit INTEGER)");
            db.execSQL("CREATE TABLE " + JOURNEYS_TABLE
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, start INTEGER,"
                    + " end INTEGER, steps INTEGER, number INTEGER)");
            db.execSQL("CREATE TABLE " + JOURNEYSTEPS_TABLE
                    + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, activity TEXT,"
                    + " repetitions INTEGER, journey INTEGER, next INTEGER)");

            createTriggers(db);
        }

        public void createTriggers(final SQLiteDatabase db) {
            db.execSQL("CREATE TRIGGER fkd_journeys_place_id BEFORE DELETE ON "
                    + LOCATIONS_TABLE + " FOR EACH ROW BEGIN "
                    + "DELETE FROM " + JOURNEYS_TABLE + " WHERE start = OLD._id"
                    + " OR end = OLD._id; END;");
            db.execSQL("CREATE TRIGGER fkd_journey_steps_journey_id BEFORE DELETE ON "
                    + JOURNEYS_TABLE + " FOR EACH ROW BEGIN "
                    + "DELETE FROM " + JOURNEYSTEPS_TABLE + " WHERE journey = OLD._id"
                    + "; END;");
        }

        /** {@inheritDoc} */
        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                final int newVersion) {
            Log.i(getClass().getSimpleName(), "Upgrading DB " + oldVersion + "->" + newVersion);
            if (oldVersion <= 2) {
                db.execSQL("DROP TABLE " + LOCATIONS_TABLE);
                onCreate(db);
            } else if (oldVersion <= 6) {
                db.execSQL("DROP TABLE " + LOCATIONS_TABLE);
                db.execSQL("DROP TABLE " + JOURNEYS_TABLE);
                db.execSQL("DROP TABLE " + JOURNEYSTEPS_TABLE);
                onCreate(db);
            } else if (oldVersion <= 7) {
                createTriggers(db);
            }
            
            if (oldVersion > 6 && oldVersion <= 8) {
                db.execSQL("ALTER TABLE " + LOCATIONS_TABLE + " ADD COLUMN duration INTEGER");
                db.execSQL("ALTER TABLE " + LOCATIONS_TABLE + " ADD COLUMN times INTEGER");
                db.execSQL("ALTER TABLE " + LOCATIONS_TABLE + " ADD COLUMN lastvisit INTEGER");
            }
        }

    }

}
