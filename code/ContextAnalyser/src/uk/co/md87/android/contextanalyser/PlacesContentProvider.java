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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import uk.co.md87.android.common.model.Place;

/**
 * A content provider for places.
 * 
 * @author chris
 */
public class PlacesContentProvider extends ContentProvider {

    public static final String AUTHORITY = "uk.co.md87.android.contextanalyser.placescontentprovider";

    private static final int CODE_PLACES = 1;
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, "places", CODE_PLACES);
    }

    private DataHelper helper;

    /** {@inheritDoc} */
    @Override
    public boolean onCreate() {
        helper = new DataHelper(getContext());
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(final Uri uri, final String[] projection,
            final String selection, final String[] selectionArgs,
            final String sortOrder) {
        if (URI_MATCHER.match(uri) != CODE_PLACES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return helper.getDatabase().query(DataHelper.LOCATIONS_TABLE,
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    /** {@inheritDoc} */
    @Override
    public String getType(final Uri uri) {
        if (URI_MATCHER.match(uri) != CODE_PLACES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return "vnd.android.cursor." + (ContentUris.parseId(uri) == -1 ? "dir" : "item")
                + Place.CONTENT_TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        if (URI_MATCHER.match(uri) != CODE_PLACES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        long rowId = helper.getDatabase().insert(DataHelper.LOCATIONS_TABLE, Place.NAME, values);

        if (rowId > 0) {
            final Uri placeUri = ContentUris.withAppendedId(Place.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(placeUri, null);
            return placeUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        if (URI_MATCHER.match(uri) != CODE_PLACES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        final int count = helper.getDatabase().delete(DataHelper.LOCATIONS_TABLE, where, whereArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        if (URI_MATCHER.match(uri) != CODE_PLACES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        final int count = helper.getDatabase().update(DataHelper.LOCATIONS_TABLE,
                values, where, whereArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
