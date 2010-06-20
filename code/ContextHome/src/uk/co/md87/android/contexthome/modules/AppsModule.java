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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import uk.co.md87.android.contexthome.DataHelper;
import uk.co.md87.android.contexthome.Module;
import uk.co.md87.android.contexthome.R;

/**
 * A module which displays application shortcuts.
 *
 * @author chris
 */
public class AppsModule extends Module {

    public AppsModule(DataHelper helper) {
        super(helper);
    }

    /** {@inheritDoc} */
    @Override
    public View getView(final Context context, final int weight) {
        final View view = View.inflate(context, R.layout.scroller, null);
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.content);
        final PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        final View.OnClickListener listener = new View.OnClickListener() {

            public void onClick(View view) {
                final ActivityInfo info = (ActivityInfo) view.getTag();
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.setClassName(info.packageName, info.name);
                context.startActivity(intent);
            }
        };

        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        
        for (ResolveInfo res : pm.queryIntentActivities(intent, 0)) {
            final ImageView image = new ImageView(context);
            image.setImageDrawable(res.activityInfo.loadIcon(pm));
            image.setFocusable(true);
            image.setClickable(true);
            image.setTag(res.activityInfo);
            image.setOnClickListener(listener);
            image.setPadding(2, 2, 2, 2);
            image.setBackgroundResource(R.drawable.grid_selector);
            layout.addView(image, 52, 52);
        }

        return view;
    }

}
