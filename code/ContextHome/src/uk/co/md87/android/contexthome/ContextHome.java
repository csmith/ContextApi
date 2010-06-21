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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Arrays;

import uk.co.md87.android.contexthome.contexts.*;
import uk.co.md87.android.contexthome.modules.*;

/**
 * A home screen that displays e-mails, text messages, etc, and responds to
 * the user's current context.
 *
 * @author chris
 */
public class ContextHome extends Activity implements Runnable {

    public static int TAG_SCORE = 10;

    private LinearLayout layout, fixedLayout;

    private ContextType[] contexts;

    private DataHelper helper;

    private int previousCount = 0;

    private Module[] modules, fixedModules;

    private final Handler handler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        contexts = new ContextType[]{
            new GlobalContext(), new HourContext(), new PeriodContext(),
            new ActivityContext(this), new DestinationContext(this),
            new LocationContext(this)
        };

        helper = new DataHelper(this, Arrays.asList(contexts));

        fixedModules = new Module[] {
            new ContactsModule(helper), new AppsModule(helper)
        };

        modules = new Module[]{
            new EmailModule(helper), new SmsModule(helper),
        };

        setContentView(R.layout.container);
        
        layout = (LinearLayout) findViewById(R.id.content);
        fixedLayout = (LinearLayout) findViewById(R.id.fixedcontent);

        initLayout();
    }

    private void initLayout() {
        fixedLayout.removeAllViews();
        layout.removeAllViews();

        long start, end;

        for (Module module : fixedModules) {
            start = System.currentTimeMillis();
            module.addViews(fixedLayout, this, 1);
            end = System.currentTimeMillis();
            Log.v("ContextHome", module.getClass().getSimpleName() + ": " + (end - start));
        }

        for (Module module : modules) {
            start = System.currentTimeMillis();
            module.addViews(layout, this, 10);
            end = System.currentTimeMillis();
            Log.v("ContextHome", module.getClass().getSimpleName() + ": " + (end - start));
        }

        handler.postDelayed(this, 1000);
    }

    public boolean sortModules() {
        boolean changed = false;
        
        int lastScore = Integer.MAX_VALUE;
        
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View view = layout.getChildAt(i);

            if (view.getTag(R.id.score) == null) {
                Log.w("ContextHome", "Removing view without score");
                layout.removeViewAt(i);
                i--;
                continue;
            }

            final int score = (Integer) view.getTag(R.id.score);

            while (score > lastScore) {
                // TODO: This could be optimised quite a lot
                changed = true;
                layout.removeViewAt(i);
                layout.addView(view, --i);
                lastScore = i == 0 ? Integer.MAX_VALUE
                        : (Integer) layout.getChildAt(i - 1).getTag(R.id.score);
            }

            lastScore = (Integer) layout.getChildAt(i).getTag(R.id.score);
        }

        changed |= layout.getChildCount() > previousCount;
        previousCount = layout.getChildCount();

        return changed;
    }

    public void run() {
        if (sortModules()) {
            handler.postDelayed(this, 1000);
        }
    }

}
