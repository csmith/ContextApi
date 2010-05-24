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

package uk.co.md87.dsp.extractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author chris
 */
public class Windower {

    private static final int WINDOW_SIZE = 128;

    private final List<float[]> data;

    public Windower(final List<float[]> data) {
        this.data = new ArrayList<float[]>(data);
    }

    public Set<Window> getWindows() {
        final Set<Window> windows = new HashSet<Window>();

        for (int i = 0; i + WINDOW_SIZE <= data.size(); i += WINDOW_SIZE) {
            windows.add(createWindow(i));

            // Overlapping frames
            if (i + WINDOW_SIZE * 1.5 <= data.size()) {
                windows.add(createWindow(i + WINDOW_SIZE / 2));
            }
        }

        return windows;
    }

    protected Window createWindow(final int offset) {
        return new Window(data.subList(offset, offset + WINDOW_SIZE));
    }

}
