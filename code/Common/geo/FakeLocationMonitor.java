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

package uk.co.md87.android.common.geo;

/**
 * A dummy location monitor for use in emulator testing.
 * 
 * @author chris
 */
public class FakeLocationMonitor implements LocationMonitor {
   
    private int count = 0;
    private double[][] points = new double[][] {
        {51.481386d, -0.084667d}, // Place 1
        {51.481386d, -0.084667d},
        {51.481386d, -0.084667d},

        {51.491386d, -0.082667d},
        {51.501386d, -0.081667d},

        {51.517676d, -0.07997d}, // Place 2
        {51.517676d, -0.07997d},
        {51.517676d, -0.07997d},

        {51.514386d, -0.08300d},
        {51.511386d, -0.08500d},
        {51.511386d, -0.09000d},
        {51.511386d, -0.10000d},
        {51.511386d, -0.12000d},
        {51.511386d, -0.14000d},
        {51.511386d, -0.17000d},

        {51.498725d, -0.17950d}, // Place 3
        {51.498725d, -0.17950d},
        {51.498725d, -0.17950d},

        {51.481386d, -0.084667d}, // Place 1
        {51.481386d, -0.084667d},
        {51.481386d, -0.084667d},

        {51.491386d, -0.082667d},
        {51.501386d, -0.081667d},

        {51.517676d, -0.07997d}, // Place 2
        {51.517676d, -0.07997d},
        {51.517676d, -0.07997d},

        {51.514386d, -0.08300d},
        {51.511386d, -0.08500d},
        {51.511386d, -0.09000d},
        {51.511386d, -0.10000d},
        {51.511386d, -0.12000d},
        {51.511386d, -0.14000d},
        {51.511386d, -0.17000d},

        {51.481386d, -0.084667d}, // Place 1
        {51.481386d, -0.084667d},
        {51.481386d, -0.084667d},
    };

    public float getAccuracy() {
        return 0f;
    }

    public double getLat() {
        return points[count++ % points.length][0];
    }

    public double getLon() {
        return points[count % points.length][1];
    }

}
