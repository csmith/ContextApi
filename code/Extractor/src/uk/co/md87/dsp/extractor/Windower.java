/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor;

import java.util.ArrayList;
import java.util.Arrays;
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
