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

import java.util.HashMap;
import java.util.Map;

/**
 * Facilitates accessing the SQLite database used for storing places.
 *
 * @author chris
 */
public class DataHelper {

    public static final String LOCATIONS_TABLE = "locations";
    private static final String DATABASE_NAME = "contextapi.db";
    private static final int DATABASE_VERSION = 2;

    private static final String INSERT_LOCATION = "insert into "
      + LOCATIONS_TABLE + "(name, lat, lon) values (?, ?, ?)";
    private static final String UPDATE_LOCATION = "update "
      + LOCATIONS_TABLE + " set name = ? where _id = ?";
    private static final String UNNAMED_QUERY = "name LIKE '%.%,%.%'";
    private static final String LOCATION_QUERY = "lat > %1$s - 0.005 and "
            + "lat < %1$s + 0.005 and lon > %2$s - 0.01 and lon < %2$s + 0.01";

    private final SQLiteStatement insertLocationStatement, updateLocationStatement;

    private final Context context;
    private SQLiteDatabase db;

    public DataHelper(final Context context) {
        this.context = context;

        final OpenHelper helper = new OpenHelper(context);
        this.db = helper.getWritableDatabase();
        this.insertLocationStatement = db.compileStatement(INSERT_LOCATION);
        this.updateLocationStatement = db.compileStatement(UPDATE_LOCATION);
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

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return results;
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
                    return new Place(cursor.getLong(0), cursor.getString(1),
                            cursor.getDouble(2), cursor.getDouble(3));
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return null;
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
        }

        /** {@inheritDoc} */
        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                final int newVersion) {
            Log.i(getClass().getSimpleName(), "Upgrading DB " + oldVersion + "->" + newVersion);
            if (oldVersion <= 1) {
                db.execSQL("DROP TABLE " + LOCATIONS_TABLE);
                onCreate(db);
            }
        }

    }

}
