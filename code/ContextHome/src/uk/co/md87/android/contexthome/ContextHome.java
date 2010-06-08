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
import uk.co.md87.android.contexthome.modules.SmsModule;

/**
 * A home screen that displays e-mails, text messages, etc, and responds to
 * the user's current context.
 *
 * @author chris
 */
public class ContextHome extends Activity {

    private static final LayoutParams MODULE_PARAMS
            = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

    private LinearLayout layout;
    private final Module[] modules = new Module[]{
        new SmsModule(), new SmsModule(), new SmsModule(), new SmsModule(),
        new SmsModule(), new SmsModule(), new SmsModule(), new SmsModule(),
        new SmsModule(), new SmsModule(),
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        layout = new LinearLayout(this);
        MODULE_PARAMS.weight = (float) 1 / 10;
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        initLayout();
    }

    private void initLayout() {
        layout.removeAllViews();

        for (Module module : modules) {
            layout.addView(module.getView(this, 1), MODULE_PARAMS);
        }
    }

}
