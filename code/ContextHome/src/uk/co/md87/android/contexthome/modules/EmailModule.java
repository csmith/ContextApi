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
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.co.md87.android.contexthome.ContextHome;
import uk.co.md87.android.contexthome.DataHelper;

import uk.co.md87.android.contexthome.Module;
import uk.co.md87.android.contexthome.R;

/**
 * A module which displays SMS messages.
 *
 * @author chris
 */
public class EmailModule extends Module implements Comparator<Email> {

    public EmailModule(DataHelper helper) {
        super(helper);
    }

    /** {@inheritDoc} */
    @Override
    public void addViews(final ViewGroup parent, final Context context, final int weight) {
        final Handler handler = new Handler();

        new Thread(new Runnable() {

            public void run() {
        final Uri inboxUri = Uri.parse("content://gmail-ls/"
            + "conversations/" + context.getSharedPreferences("email",
            Context.MODE_WORLD_READABLE).getString("account", "chris87@gmail.com"));

        final Cursor cursor = context.getContentResolver().query(inboxUri,
                new String[] { "conversation_id" }, null, null, null);

        while (cursor.getExtras().getString("status").equals("LOADING")) {
            cursor.requery();
            Thread.yield();
        }

        final int convIdIndex = cursor.getColumnIndex("conversation_id");

        final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        if (cursor.moveToFirst()) {
            do {
                final View view = View.inflate(context, R.layout.titledimage, null);
                view.setClickable(true);
                view.setFocusable(true);
                view.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {
                        final Intent intent = new Intent();
                        intent.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivity");
                        context.startActivity(intent);

                        recordAction(getMap((Email) arg0.getTag()));
                    }
                });

                final Email message = new Email(handler, context, EmailModule.this,
                        cursor.getLong(convIdIndex), view);
                view.setTag(message);
                updateScore(message);

        handler.post(new Runnable() {

                    public void run() {
            parent.addView(message.getView(), params);
                    }
                });
            } while (cursor.moveToNext());
        }

        cursor.close();
                    }
        }).start();
    }

    public void updateScore(final Email email) {
        email.getView().setTag(R.id.score, getScore(getMap(email)));
    }

    public Map<String, String> getMap(final Email email) {
        final Map<String, String> params = new HashMap<String, String>();
        if (email.getId() >= 0) {
            params.put("contactid", String.valueOf(email.getId()));
        }

        params.put("contactemail", email.getAddress());

        return params;
    }

    public int compare(final Email arg0, final Email arg1) {
        return getScore(getMap(arg1)) - getScore(getMap(arg0));
    }

}
