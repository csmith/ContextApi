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
import android.widget.LinearLayout.LayoutParams;
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
public class ContextHome extends Activity {

    private static final LayoutParams MODULE_PARAMS
            = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

    private LinearLayout layout, fixedLayout;

    private final ContextType[] contexts = new ContextType[]{
        new GlobalContext(), new HourContext(), new PeriodContext()
    };

    private final DataHelper helper = new DataHelper(this, Arrays.asList(contexts));

    private final Module[] fixedModules = new Module[] {
        new ContactsModule(helper), new AppsModule(helper)
    };

    private final Module[] modules = new Module[]{
        new EmailModule(helper), new SmsModule(helper),
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.container);
        
        layout = (LinearLayout) findViewById(R.id.content);
        fixedLayout = (LinearLayout) findViewById(R.id.fixedcontent);

        initLayout();
    }

    private void initLayout() {
        fixedLayout.removeAllViews();
        layout.removeAllViews();

        for (Module module : fixedModules) {
            fixedLayout.addView(module.getView(this, 1), MODULE_PARAMS);
        }

        for (Module module : modules) {
            layout.addView(module.getView(this, 10), MODULE_PARAMS);
        }
    }

}
