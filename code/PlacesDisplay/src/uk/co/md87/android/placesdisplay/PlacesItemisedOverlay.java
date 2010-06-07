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

package uk.co.md87.android.placesdisplay;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Itemised overlay of places.
 *
 * @author chris
 */
public class PlacesItemisedOverlay extends ItemizedOverlay<OverlayItem> {

    private final Context context;
    private final List<OverlayItem> overlays = new ArrayList<OverlayItem>();

    public PlacesItemisedOverlay(Context context, Drawable defaultMarker) {
        super(boundCenter(defaultMarker));
        
        this.context = context;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return overlays.get(i);
    }

    public void addOverlay(OverlayItem overlay) {
        overlays.add(overlay);
        populate();
    }

    @Override
    protected boolean onTap(int arg0) {
        Toast.makeText(context, overlays.get(arg0).getTitle() + "\n\n"
                + overlays.get(arg0).getSnippet(), Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public int size() {
        return overlays.size();
    }

}
