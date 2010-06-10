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
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import java.util.HashMap;
import java.util.Map;

import uk.co.md87.android.contextapi.ContextApi;
import uk.co.md87.android.contextapi.ContextApi.Journeys.ColumnNames;

/**
 * An overlay for showing journeys between places.
 *
 * @author chris
 */
public class JourneysOverlay extends Overlay {

    private final Map<OverlayItem, Map<OverlayItem, Integer>> journeys
            = new HashMap<OverlayItem, Map<OverlayItem, Integer>>();
    private int max = 0;

    public JourneysOverlay(final Context context, final Map<Integer, OverlayItem> places) {
        final Cursor cursor = context.getContentResolver().query(ContextApi.Journeys.CONTENT_URI,
                new String[] { ColumnNames.START, ColumnNames.END, ColumnNames.NUMBER },
                null, null, null);

        if (cursor.moveToFirst()) {
            final int startColumn = cursor.getColumnIndex(ColumnNames.START);
            final int endColumn = cursor.getColumnIndex(ColumnNames.END);
            final int numberColumn = cursor.getColumnIndex(ColumnNames.NUMBER);

            do {
                final int start = cursor.getInt(startColumn);
                final int end = cursor.getInt(endColumn);
                final OverlayItem a = places.get(Math.min(start, end)),
                        b = places.get(Math.max(start, end));

                if (!journeys.containsKey(a)) {
                    journeys.put(a, new HashMap<OverlayItem, Integer>());
                }

                final Map<OverlayItem, Integer> map = journeys.get(a);

                if (!map.containsKey(b)) {
                    map.put(b, 0);
                }

                final int total = map.get(b) + cursor.getInt(numberColumn);
                map.put(b, total);
                max = Math.max(max, total);
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        super.draw(canvas, map, shadow);

        final Paint paint = new Paint();
        paint.setARGB(255, 255, 0, 0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        for (Map.Entry<OverlayItem, Map<OverlayItem, Integer>> start
                : journeys.entrySet()) {
            final Point spoint = map.getProjection().toPixels(start.getKey().getPoint(), null);
            for (Map.Entry<OverlayItem, Integer> entry : start.getValue().entrySet()) {
                final Point epoint = map.getProjection().toPixels(entry.getKey().getPoint(), null);

                final Paint mypaint = new Paint(paint);

                mypaint.setStrokeJoin(Paint.Join.ROUND);
                mypaint.setStrokeCap(Paint.Cap.ROUND);
                mypaint.setStrokeWidth(10 * entry.getValue() / (float) max);

                canvas.drawLine(spoint.x, spoint.y, epoint.x, epoint.y, mypaint);
            }
        }
    }

}
