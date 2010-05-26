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

package uk.co.md87.android.common.aggregator;

import java.util.HashMap;
import java.util.Map;

/**
 * Aggregates a stream of classifications and performs averaging in order to
 * smooth out results. Each classification supplied to the aggregator is used
 * to alter the 'probabilities' of that classification holding. These
 * probabilities are calculated hierarchically, and the aggregator can in some
 * cases produce a result that is not in itself a normal activity because of
 * this (for example, it may classify unclear data as
 * <code>CLASSIFIED/VEHICLE</code>, rather than as a sub-type as expected).
 *
 * @author chris
 */
public class Aggregator {

    static final double DELTA = 0.25;
    static final double THRESHOLD = 0.5;

    private final HashMap<String, HashMap<String, Double>> scores
            = new HashMap<String, HashMap<String, Double>>() {{
        put("", new HashMap<String, Double>() {{
            put("null", 0.5d);
            put("CLASSIFIED", 0.5d);
        }});

        put("CLASSIFIED", new HashMap<String, Double>() {{
            put("null", 0.2d);
            put("DANCING", 0.2d);
            put("WALKING", 0.2d);
            put("VEHICLE", 0.2d);
            put("IDLE", 0.2d);
        }});

        put("CLASSIFIED/WALKING", new HashMap<String, Double>() {{
            put("null", 0.5d);
            put("STAIRS", 0.5d);
        }});

        put("CLASSIFIED/VEHICLE", new HashMap<String, Double>() {{
            put("null", 0.333d);
            put("CAR", 0.333d);
            put("BUS", 0.333d);
        }});

        put("CLASSIFIED/IDLE", new HashMap<String, Double>() {{
            put("null", 0.333d);
            put("STANDING", 0.333d);
            put("SITTING", 0.333d);
        }});

        put("CLASSIFIED/WALKING/STAIRS", new HashMap<String, Double>() {{
            put("null", 0.333d);
            put("UP", 0.333d);
            put("DOWN", 0.333d);
        }});
    }};

    public void addClassification(final String classification) {
        String path = "";

        for (String part : classification.split("/")) {
            if (!scores.containsKey(path)) {
                throw new RuntimeException("Path not found: " + path
                        + " (classification: " + classification + ")");
            }

            updateScores(scores.get(path), part);
            path = path + (path.length() == 0 ? "" : "/") + part;
        }

        if (scores.containsKey(path)) {
            // This classification has children which we're not using
            // e.g. we've received CLASSIFIED/WALKING, but we're not walking
            //      up or down stairs
            updateScores(scores.get(path), "null");
        }
    }

    void updateScores(final Map<String, Double> map, final String target) {
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            //Log.d(getClass().getName(), "Score for " + entry.getKey() + " was: " + entry.getValue());
            entry.setValue(entry.getValue() * (1 - DELTA));

            if (entry.getKey().equals(target)) {
                entry.setValue(entry.getValue() + DELTA);
            }
            //Log.d(getClass().getName(), "Score for " + entry.getKey() + " is now: " + entry.getValue());
        }
    }

    public String getClassification() {
        String path = "";

        do {
            final Map<String, Double> map = scores.get(path);
            double best = THRESHOLD;
            String bestPath = "null";

            for (Map.Entry<String, Double> entry : map.entrySet()) {
                if (entry.getValue() >= best) {
                    best = entry.getValue();
                    bestPath = entry.getKey();
                }
            }

            path = path + (path.length() == 0 ? "" : "/") + bestPath;
        } while (scores.containsKey(path));

        return path.replaceAll("(^CLASSIFIED)?/?null$", "");
    }

}
