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

package uk.co.md87.android.contexthome.modules;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import uk.co.md87.android.contexthome.Module;

/**
 * A module which displays SMS messages.
 *
 * @author chris
 */
public class SmsModule implements Module {

    private static final Uri INBOX_URI = Uri.parse("content://sms/inbox");

    /** {@inheritDoc} */
    @Override
    public View getView(final Context context, final int weight) {
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final Cursor cursor = context.getContentResolver().query(INBOX_URI,
                new String[] { "_id", "date", "body" }, null, null, "date DESC");
        boolean success = cursor.moveToFirst();
        for (int i = 0; i < weight && success; i++) {
            final TextView text = new TextView(context);
            text.setText(cursor.getString(cursor.getColumnIndex("body")));
            layout.addView(text);
            success = cursor.moveToNext();
        }
        cursor.close();
        

        return layout;
    }

}
