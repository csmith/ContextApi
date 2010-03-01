/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.md87.dsp.extractor;

import java.util.List;

/**
 *
 * @author chris
 */
public class Window {

    private final List<float[]> data;

    public Window(List<float[]> data) {
        this.data = data;
    }

    public List<float[]> getData() {
        return data;
    }

}
