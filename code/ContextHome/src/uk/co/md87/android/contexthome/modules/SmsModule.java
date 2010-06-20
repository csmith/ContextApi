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
import android.os.Handler;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import uk.co.md87.android.contexthome.ContextHome;

import uk.co.md87.android.contexthome.DataHelper;
import uk.co.md87.android.contexthome.Module;
import uk.co.md87.android.contexthome.R;

/**
 * A module which displays SMS messages.
 *
 * @author chris
 */
public class SmsModule extends Module {

    private static final Uri INBOX_URI = Uri.parse("content://sms/inbox");

    public SmsModule(DataHelper helper) {
        super(helper);
    }

    /** {@inheritDoc} */
    @Override
    public void addViews(final ViewGroup parent, final Context context, final int weight) {
        final Handler handler = new Handler();

        new Thread(new Runnable() {

            public void run() {
        final Cursor cursor = context.getContentResolver().query(INBOX_URI,
                new String[] { "thread_id", "date", "body", "address" }, null, null, "date DESC");
        final int idIndex = cursor.getColumnIndex("thread_id");
        final int bodyIndex = cursor.getColumnIndex("body");
        final int addressIndex = cursor.getColumnIndex("address");

        boolean success = cursor.moveToFirst();
        for (int i = 0; i < weight && success; i++) {
            final String body = cursor.getString(bodyIndex);
            final String address = cursor.getString(addressIndex);

            final View view = getView(context, handler, body, address, cursor.getLong(idIndex));
            view.setTag(R.id.score, 0); // TODO

        handler.post(new Runnable() {

                    public void run() {
                        parent.addView(view);
                    }
                });

            success = cursor.moveToNext();
        }
        cursor.close();
       
                    }
        }).start();
    }

    private View getView(final Context context, final Handler handler,
            final String text, final String address, final long threadId) {
        final View view = View.inflate(context, R.layout.titledimage, null);
        view.setClickable(true);
        view.setFocusable(true);
        view.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("content://mms-sms/conversations/" + threadId));
                context.startActivity(intent);
            }
        });

        new Thread(new Runnable() {

            public void run() {
          final Uri contactUri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL,
                Uri.encode(address));

        final Cursor cursor = context.getContentResolver().query(contactUri,
                new String[] { Contacts.Phones.PERSON_ID,
                Contacts.Phones.DISPLAY_NAME }, null, null, null);

        final TextView title = (TextView) view.findViewById(R.id.title);
        final ImageView image = (ImageView) view.findViewById(R.id.image);

        if (cursor.moveToFirst()) {
            final Uri uri = ContentUris.withAppendedId(People.CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndex(Contacts.Phones.PERSON_ID)));
            final String name = cursor.getString(cursor
                    .getColumnIndex(Contacts.Phones.DISPLAY_NAME));
            handler.post(new Runnable() {

                        public void run() {
            title.setText(Html.fromHtml("<b>" + name + "</b>"));
            image.setImageBitmap(Contacts.People.loadContactPhoto(context,
                    uri, R.drawable.blank, null));
                                    }
                    });
        } else {
            title.setText(Html.fromHtml("<b>" + address + "</b>"));
        }

        cursor.close();

        final TextView body = (TextView) view.findViewById(R.id.body);

                    handler.post(new Runnable() {

                        public void run() {
        body.setText(text);
                        }
                    });

                  }
        }).start();

        return view;
    }

}
