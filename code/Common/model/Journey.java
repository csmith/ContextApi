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

package uk.co.md87.android.common.model;

import android.net.Uri;

/**
 * A journey is a sequence of actions which were observed as the user travelled
 * between one known {@link Place} and another.
 *
 * @author chris
 */
public class Journey {

    public static final String _ID = "_id";
    public static final String START = "start";
    public static final String END = "end";
    public static final String STEPS = "steps";
    public static final String NUMBER = "number";

    public static final Uri CONTENT_URI = Uri.parse("content://uk.co.md87"
            + ".android.contextanalyser.journeyscontentprovider/journeys");
    public static final String CONTENT_TYPE = "vnd.contextanalyser.journey";

    private final long id;
    private final long start;
    private final long end;
    private final int steps;
    private final int number;

    public Journey(final long id, final long start, final long end,
            final int steps, final int number) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.steps = steps;
        this.number = number;
    }

    public long getEnd() {
        return end;
    }

    public long getId() {
        return id;
    }

    public long getStart() {
        return start;
    }

    public int getSteps() {
        return steps;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "Journey{" + "id=" + id + " start=" + start + " end=" + end
                + " steps=" + steps + " number=" + number + '}';
    }

}
