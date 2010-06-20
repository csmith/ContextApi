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

package uk.co.md87.android.contexthome;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facilitates accessing the SQLite database used for storing historical
 * interaction information.
 *
 * @author chris
 */
public class DataHelper {

    private static final String DATABASE_NAME = "contexthome.db";
    private static final int DATABASE_VERSION = 1;

    private static final String INSERT_ACTION = "insert into actions ("
      + "module, actiontype, actionvalue, contexttype, contextvalue, number) "
      + "VALUES (?, ?, ?, ?, ?, 0)";
    private static final String UPDATE_ACTION = "update actions set number = number + 1"
      + " WHERE module = ? AND actiontype = ? AND actionvalue = ?"
      + " AND contexttype = ? AND contextvalue = ?";

    private final List<ContextType> contexts;
    private final SQLiteDatabase db;
    private final SQLiteStatement insertActionStatement,
            updateActionStatement;

    public DataHelper(final Context context, final List<ContextType> contexts) {
        final OpenHelper helper = new OpenHelper(context);
        this.db = helper.getWritableDatabase();
        this.contexts = contexts;

        this.insertActionStatement = db.compileStatement(INSERT_ACTION);
        this.updateActionStatement = db.compileStatement(UPDATE_ACTION);
    }

    public void registerAction(final String module, final Map<String, String> actions) {
        // TODO: It'd be nice if the two statements could be merged
        insertActionStatement.bindString(0, module);
        updateActionStatement.bindString(0, module);

        for (Map.Entry<String, String> action : actions.entrySet()) {
            insertActionStatement.bindString(1, action.getKey());
            insertActionStatement.bindString(2, action.getValue());
            updateActionStatement.bindString(1, action.getKey());
            updateActionStatement.bindString(2, action.getValue());

            for (ContextType context : contexts) {
                insertActionStatement.bindString(3, context.getName());
                insertActionStatement.bindString(4, context.getValue());
                updateActionStatement.bindString(3, context.getName());
                updateActionStatement.bindString(4, context.getValue());
                insertActionStatement.execute();
                updateActionStatement.execute();
            }
        }
    }

    public Map<String, Map<String, Integer>> getActions(final String module) {
        final Map<String, Map<String, Integer>> res = new HashMap<String, Map<String, Integer>>();

        final StringBuilder query = new StringBuilder("SELECT sum(number) AS total, "
                + " actiontype, actionvalue FROM actions WHERE module = ? AND (0");
        final String[] params = new String[1 + contexts.size() * 2];
        final String extraQuery = " OR (contexttype = ? AND contextvalue = ?)";

        int i = 1;
        for (ContextType context : contexts) {
            params[i++] = context.getName();
            params[i++] = context.getValue();
            query.append(extraQuery);
        }

        query.append(") GROUP BY contexttype, contextvalue ORDER BY total DESC");
        final Cursor cursor = db.rawQuery(query.toString(), params);

        if (cursor.moveToFirst()) {
            final int totalColumn = cursor.getColumnIndex("total");
            final int typeColumn = cursor.getColumnIndex("actiontype");
            final int valueColumn = cursor.getColumnIndex("actionvalue");

            do {
                final String type = cursor.getString(typeColumn);

                if (!res.containsKey(type)) {
                    res.put(type, new HashMap<String, Integer>());
                }

                res.get(type).put(cursor.getString(valueColumn), cursor.getInt(totalColumn));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return res;
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(final Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL("CREATE TABLE actions (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "module TEXT, actiontype TEXT, actionvalue TEXT, contexttype TEXT, "
                    + "contextvalue TEXT, number INTEGER, UNIQUE (module, actiontype, "
                    + "actionvalue, contexttype, contextvalue) "
                    + "ON CONFLICT IGNORE)");
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                final int newVersion) {
            db.execSQL("DROP TABLE actions");
            onCreate(db);
        }
    }

}
