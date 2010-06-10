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

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.Contacts.Photos;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import uk.co.md87.android.contexthome.Module;
import uk.co.md87.android.contexthome.R;

/**
 * A module which displays contact shortcuts.
 *
 * @author chris
 */
public class ContactsModule implements Module {

    /** {@inheritDoc} */
    @Override
    public View getView(final Context context, final int weight) {
        final View view = View.inflate(context, R.layout.scroller, null);
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.content);

        final View.OnClickListener listener = new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType(People.CONTENT_ITEM_TYPE);
                intent.setData((Uri) view.getTag());
                context.startActivity(intent);

            }
        };

        final Cursor cursor = context.getContentResolver().query(Photos.CONTENT_URI,
                new String[] { "person" }, "exists_on_server != 0", null, null);

        final int column = cursor.getColumnIndex("person");
        if (cursor.moveToFirst()) {
            do {
                layout.addView(getView(context, listener, cursor.getLong(column)), 52, 52);
            } while (cursor.moveToNext());
        }

        return view;
    }

    private final View getView(final Context context, View.OnClickListener listener,
            final long id) {
        final ImageView image = new ImageView(context);
        image.setFocusable(true);
        image.setClickable(true);
        Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, id);
        image.setTag(uri);
        image.setImageBitmap(People.loadContactPhoto(context,
                    uri, R.drawable.blank, null));
        image.setOnClickListener(listener);
        image.setBackgroundResource(R.drawable.grid_selector);
        image.setPadding(2, 2, 2, 2);

        return image;
    }

}