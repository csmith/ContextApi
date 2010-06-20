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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.md87.android.contexthome.DataHelper;
import uk.co.md87.android.contexthome.Module;
import uk.co.md87.android.contexthome.R;

/**
 * A module which displays application shortcuts.
 *
 * @author chris
 */
public class AppsModule extends Module implements Comparator<ResolveInfo> {

    public AppsModule(DataHelper helper) {
        super(helper);
    }

    /** {@inheritDoc} */
    @Override
    public void addViews(final ViewGroup parent, final Context context, final int weight) {
        final View view = View.inflate(context, R.layout.scroller, null);
        final Handler handler = new Handler();

        new Thread(new Runnable() {

            public void run() {
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.content);
        final PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        final View.OnClickListener listener = new View.OnClickListener() {

            public void onClick(View view) {
                final ResolveInfo info = (ResolveInfo) view.getTag();
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                context.startActivity(intent);

                recordAction(getMap(info));
            }
        };

        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        Collections.sort(infos, AppsModule.this);

        final List<View> views = new ArrayList<View>(infos.size());
        
        for (ResolveInfo res : infos) {
            final ImageView image = new ImageView(context);
            image.setImageDrawable(res.activityInfo.loadIcon(pm));
            image.setFocusable(true);
            image.setClickable(true);
            image.setTag(res);
            image.setOnClickListener(listener);
            image.setPadding(2, 2, 2, 2);
            image.setBackgroundResource(R.drawable.grid_selector);
            views.add(image);
        }

        handler.post(new Runnable() {

                    public void run() {
                        for (View myView : views) {
                            layout.addView(myView, 52, 52);
                        }
                    }
                });
        }
        }).start();

        parent.addView(view);
    }

    public Map<String, String> getMap(final ResolveInfo info) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("app", info.activityInfo.packageName);

        return params;
    }

    public int compare(final ResolveInfo arg0, final ResolveInfo arg1) {
        return getScore(getMap(arg1)) - getScore(getMap(arg0));
    }

}
