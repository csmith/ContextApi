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

package uk.co.md87.android.contextanalyser;

import java.util.LinkedList;
import java.util.List;
import uk.co.md87.android.common.model.JourneyStep;

/**
 * Provides utility methods relating to journeys.
 * 
 * @author chris
 */
public class JourneyUtil {

    public static List<JourneyStep> getSteps(final List<String> activities) {
        final List<JourneyStep> steps = new LinkedList<JourneyStep>();

        String last = null;
        int count = 0;

        for (String activity : activities) {
            if (activity.equals(last)) {
                count++;
            } else {
                if (last != null) {
                    steps.add(new JourneyStep(last, count));
                }

                count = 1;
                last = activity;
            }
        }

        if (last != null) {
            steps.add(new JourneyStep(last, count));
        }

        return steps;
    }

    public static boolean isCompatible(final List<JourneyStep> incomplete,
            final List<JourneyStep> target) {
        if (target.size() < incomplete.size()) {
            return false;
        }
        
        for (int i = 0; i < incomplete.size(); i++) {
            final JourneyStep targetStep = target.get(i);
            final JourneyStep incompleteStep = incomplete.get(i);

            if (!targetStep.getActivity().equals(incompleteStep.getActivity()) ||
                    // ^ One of the activities is different
                    (i < incomplete.size() - 1 && incompleteStep.getRepetitions()
                    < Math.floor(targetStep.getRepetitions() * 0.5)) ||
                    // ^ Or a completed step is too short
                    (incompleteStep.getRepetitions() > Math.ceil(targetStep.getRepetitions() * 1.5))
                    // ^ Or any step is too long
                    ) {
                return false;
            }
        }

        return true;
    }

}
