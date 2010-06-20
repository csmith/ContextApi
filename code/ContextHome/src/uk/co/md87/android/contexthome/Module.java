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

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

/**
 * A module which can be displayed on the context-aware home screen.
 *
 * @author chris
 */
public abstract class Module {

    private final String module;
    private final DataHelper helper;
    private Map<String, Integer> actions;

    public Module(final DataHelper helper) {
        this.module = getClass().getSimpleName().replace("Module", "").toLowerCase();
        this.helper = helper;

        refreshActions();
    }

    public void refreshActions() {
        actions = helper.getActions(module);
    }

    protected int getScore(final Map<String, String> params) {
        int total = 0;

        for (Map.Entry<String, String> pair : params.entrySet()) {
            final String key = pair.getKey() + "/" + pair.getValue();
            if (actions.containsKey(key)) {
                total += actions.get(key);
            }
        }

        return total;
    }

    protected void recordAction(final Map<String, String> params) {
        helper.registerAction(module, params);
    }

    public abstract void addViews(final ViewGroup parent, final Context context, final int weight);

}
