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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import uk.co.md87.android.contexthome.Module;
import uk.co.md87.android.contexthome.R;

/**
 * A module which displays SMS messages.
 *
 * @author chris
 */
public class EmailModule implements Module {

    /** {@inheritDoc} */
    @Override
    public View getView(final Context context, final int weight) {
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final Uri inboxUri = Uri.parse("content://gmail-ls/"
            + "conversations/" + context.getSharedPreferences("email",
            Context.MODE_WORLD_READABLE).getString("account", "chris87@gmail.com"));

        final Cursor cursor = context.getContentResolver().query(inboxUri,
                new String[] { "conversation_id" }, null, null, null);

        final int convIdIndex = cursor.getColumnIndex("conversation_id");

        final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        boolean success = cursor.moveToFirst();
        for (int i = 0; i < weight && success; ) {
            final long convId = cursor.getLong(convIdIndex);
            final Uri uri = inboxUri.buildUpon().appendEncodedPath(String.valueOf(convId)
                    + "/messages").build();

            final Cursor messageCursor = context.getContentResolver().query(uri,
                new String[] { "fromAddress", "subject", "messageId" }, null, null, null);

            if (messageCursor.moveToFirst()) {
                final int subjectIndex = messageCursor.getColumnIndex("subject");
                final int addressIndex = messageCursor.getColumnIndex("fromAddress");
                final int messageIdIndex = messageCursor.getColumnIndex("messageId");

                final String body = messageCursor.getString(subjectIndex);
                final String address = messageCursor.getString(addressIndex);
                final int count = messageCursor.getCount();
                final long messageId = messageCursor.getLong(messageIdIndex);

                layout.addView(getView(context, body, address, messageId, count), params);

                i++;
            }

            messageCursor.close();
            success = cursor.moveToNext();
        }
        cursor.close();
        

        return layout;
    }

    private View getView(final Context context, String text, String address, final long messageId, int count) {
        final View view = View.inflate(context, R.layout.titledimage, null);
        view.setClickable(true);
        view.setFocusable(true);
        view.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                final Intent intent = new Intent();
                intent.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivity");
                context.startActivity(intent);
            }
        });

        final Uri contactUri = Uri.parse("content://contacts/contact_methods");

        final String name = address.replaceAll("^.*\"(.*?)\".*$", "$1")
                .replaceAll("(.*), (.*)", "$2 $1");
        final String email = address.replaceAll("^.*<(.*?)>.*$", "$1");

        final Cursor cursor = context.getContentResolver().query(contactUri,
                new String[] { "person", "name" }, "data LIKE ?", new String[] { email }, null);

        final TextView title = (TextView) view.findViewById(R.id.title);
        final ImageView image = (ImageView) view.findViewById(R.id.image);

        if (cursor.moveToFirst()) {
            title.setText(Html.fromHtml("<b>" + cursor.getString(cursor
                    .getColumnIndex("name")) + "</b> (" + count + ")"));
            Uri uri = ContentUris.withAppendedId(People.CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndex("person")));
            image.setImageBitmap(Contacts.People.loadContactPhoto(context,
                    uri, R.drawable.blank, null));
        } else {
            title.setText(Html.fromHtml("<b>" + (name.length() == 0 ? email : name) + "</b> ("
                    + count + ")"));
        }

        cursor.close();

        final TextView body = (TextView) view.findViewById(R.id.body);
        body.setText(text);

        return view;
    }

}
