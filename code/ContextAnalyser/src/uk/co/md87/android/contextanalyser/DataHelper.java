/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.android.contextanalyser;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;

/**
 *
 * @author chris
 */
public class DataHelper {

    private static final String LOCATIONS_TABLE = "locations";
    private static final String DATABASE_NAME = "contextapi.db";
    private static final int DATABASE_VERSION = 1;

    private static final String INSERT_LOCATION = "insert into "
      + LOCATIONS_TABLE + "(name, lat, lon) values (?, ?, ?)";
    private static final String LOCATION_QUERY = "lat > %1$s - 0.005 and "
            + "lat < %1$s + 0.005 and lon > %2$s - 0.01 and lon < %2$s + 0.01";

    private final SQLiteStatement insertLocationStatement;

    private final Context context;
    private SQLiteDatabase db;

    public DataHelper(final Context context) {
        this.context = context;

        final OpenHelper helper = new OpenHelper(context);
        this.db = helper.getWritableDatabase();
        this.insertLocationStatement = db.compileStatement(INSERT_LOCATION);
    }

    public long addLocation(final String name, final double lat, final double lon) {
        insertLocationStatement.bindString(1, name);
        insertLocationStatement.bindDouble(2, lat);
        insertLocationStatement.bindDouble(3, lon);
        return insertLocationStatement.executeInsert();
    }

    public LocationResult findLocation(final double lat, final double lon) {
        final Cursor cursor = db.query(LOCATIONS_TABLE,
                new String[] { "id", "name", "lat", "lon" },
                String.format(LOCATION_QUERY, lat, lon), null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                final float[] res = new float[1];
                Location.distanceBetween(lat, lon, cursor.getDouble(2), cursor.getDouble(3), res);

                if (res[0] <= 500) {
                    return new LocationResult(cursor.getLong(0), cursor.getString(1),
                            cursor.getDouble(2), cursor.getDouble(3));
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return null;
    }

    public static class LocationResult {

        private final long id;
        private final String name;
        private final double lat, lon;

        public LocationResult(long id, String name, double lat, double lon) {
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

    }

    private static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(final Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /** {@inheritDoc} */
        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + LOCATIONS_TABLE
                    + " (id INTEGER PRIMARY KEY, name TEXT, lon REAL, lat REAL)");
        }

        /** {@inheritDoc} */
        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                final int newVersion) {
            // Do nothing. Yet.
        }

    }

}
