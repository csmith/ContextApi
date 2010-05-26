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

package uk.co.md87.android.contextanalyser.model;

/**
 * A journey step is a single activity which occurred one or more times in a
 * {@link Journey}. The activity may be repeated any number of times in a row.
 *
 * @author chris
 */
public class JourneyStep {

    private final long id;
    private final String activity;
    private final int repetitions;
    private final long journey;
    private final long next;

    public JourneyStep(final long id, final String activity,
            final int repetitions, final long journey, final long next) {
        this.id = id;
        this.activity = activity;
        this.repetitions = repetitions;
        this.journey = journey;
        this.next = next;
    }

    public String getActivity() {
        return activity;
    }

    public long getId() {
        return id;
    }

    public long getJourney() {
        return journey;
    }

    public long getNext() {
        return next;
    }

    public int getRepetitions() {
        return repetitions;
    }

}