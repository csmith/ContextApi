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

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Map;

import uk.co.md87.android.contextanalyser.rpc.ContextAnalyserBinder;

/**
 * A content provider for predictions.
 * 
 * @author chris
 */
public class PredictionsContentProvider extends ContentProvider {

    public static final String AUTHORITY
            = "uk.co.md87.android.contextanalyser.predictionscontentprovider";

    private static final int CODE_DESTINATION = 1;
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, "destination", CODE_DESTINATION);
    }
    
    private ContextAnalyserBinder service;

    private final ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = ContextAnalyserBinder.Stub.asInterface(arg1);
        }

        public void onServiceDisconnected(ComponentName arg0) {
            service = null;
            onCreate();
        }
    };

    /** {@inheritDoc} */
    @Override
    public boolean onCreate() {
        getContext().bindService(new Intent(getContext(),
                ContextAnalyserService.class), connection, Context.BIND_AUTO_CREATE);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(final Uri uri, final String[] projection,
            final String selection, final String[] selectionArgs,
            final String sortOrder) {
        if (URI_MATCHER.match(uri) != CODE_DESTINATION) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (service == null) {
            throw new IllegalStateException("Unable to bind to service");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<Long, Integer> predictions = service.getPredictions();
            final MatrixCursor cursor = new MatrixCursor(new String[]{"_ID", "place", "count"},
                    predictions.size());

            int i = 0;
            for (Map.Entry<Long, Integer> prediction : predictions.entrySet()) {
                cursor.addRow(new Object[]{i++, prediction.getKey(), prediction.getValue()});
            }

            return cursor;
        } catch (RemoteException ex) {
            throw new RuntimeException("Unable to retrieve activity", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getType(final Uri uri) {
        if (URI_MATCHER.match(uri) != CODE_DESTINATION) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return "vnd.android.cursor." + (ContentUris.parseId(uri) == -1 ? "dir" : "item")
                + "vnd.contextanalyser.prediction";
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        if (URI_MATCHER.match(uri) != CODE_DESTINATION) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        throw new UnsupportedOperationException("Cannot insert");
    }

    /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        if (URI_MATCHER.match(uri) != CODE_DESTINATION) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        throw new UnsupportedOperationException("Cannot delete");
    }

    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        if (URI_MATCHER.match(uri) != CODE_DESTINATION) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        throw new UnsupportedOperationException("Cannot update");
    }

}
