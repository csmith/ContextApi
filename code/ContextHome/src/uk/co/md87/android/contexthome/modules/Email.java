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
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import uk.co.md87.android.contexthome.R;

public class Email implements Runnable {

    private String body;
    private String address;
    private int count;
    private final long convId;
    private final Handler handler;
    private final Context context;
    private final EmailModule module;
    private final View view;
    private long id = -1;

    public Email(Handler handler, Context context, EmailModule module, long convId, View view) {
        this.handler = handler;
        this.convId = convId;
        this.context = context;
        this.module = module;
        this.view = view;
        
        new Thread(this).start(); // Ick!
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public int getCount() {
        return count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public View getView() {
        return view;
    }

    public void run() {
        final Uri inboxUri = Uri.parse("content://gmail-ls/"
            + "conversations/" + context.getSharedPreferences("email",
            Context.MODE_WORLD_READABLE).getString("account", "chris87@gmail.com"));

       final Uri uri = inboxUri.buildUpon().appendEncodedPath(String.valueOf(convId)
                    + "/messages").build();

       final Cursor messageCursor = context.getContentResolver().query(uri,
            new String[] { "fromAddress", "subject", "messageId" }, null, null, null);

        while (messageCursor.getExtras().getString("status").equals("LOADING")) {
            messageCursor.requery();
            Thread.yield();
        }

        if (messageCursor.moveToFirst()) {
            final int subjectIndex = messageCursor.getColumnIndex("subject");
            final int addressIndex = messageCursor.getColumnIndex("fromAddress");

            body = messageCursor.getString(subjectIndex);
            address = messageCursor.getString(addressIndex);
            count = messageCursor.getCount();
        }

        messageCursor.close();

        final Uri contactUri = Uri.parse("content://contacts/contact_methods");

        final String name = address.replaceAll("^.*\"(.*?)\".*$", "$1")
                .replaceAll("(.*), (.*)", "$2 $1");
        final String email = address.replaceAll("^.*<(.*?)>.*$", "$1");

        final Cursor cursor = context.getContentResolver().query(contactUri,
                new String[] { "person", "name" }, "data LIKE ?", new String[] { email }, null);

        final TextView title = (TextView) view.findViewById(R.id.title);
        final ImageView image = (ImageView) view.findViewById(R.id.image);

        if (cursor.moveToFirst()) {
            final Uri personUri = ContentUris.withAppendedId(People.CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndex("person")));
            setId(cursor.getLong(cursor.getColumnIndex("person")));
            final String sender = cursor.getString(cursor.getColumnIndex("name"));
            handler.post(new Runnable() {

                public void run() {
                    title.setText(Html.fromHtml("<b>" + sender + "</b> (" + count + ")"));
                    image.setImageBitmap(Contacts.People.loadContactPhoto(context,
                        personUri, R.drawable.blank, null));
                }
            });
        } else {
            handler.post(new Runnable() {

                public void run() {
                    title.setText(Html.fromHtml("<b>" + (name.length() == 0 ? email : name)
                            + "</b> (" + getCount() + ")"));
                }
            });
        }

        cursor.close();

        module.updateScore(this);

        final TextView body = (TextView) view.findViewById(R.id.body);

        handler.post(new Runnable() {

            public void run() {
                body.setText(getBody());
            }
        });
    }
}
