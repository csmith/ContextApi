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

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runners.Parameterized.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.co.md87.android.common.model.JourneyStep;
import static org.junit.Assert.*;

/**
 * Unit tests for JourneyUtil.getSteps.
 * 
 * @author chris
 */
@RunWith(Parameterized.class)
public class JourneyUtilGetStepsTest {

    private final String[] activities;
    private final JourneyStep[] expected;

    public JourneyUtilGetStepsTest(final String[] activities, final JourneyStep[] expected) {
        this.activities = activities;
        this.expected = expected;
    }

    @Test
    public void testGetSteps() {
        final List<JourneyStep> result = JourneyUtil.getSteps(Arrays.asList(activities));

        assertEquals(Arrays.asList(expected), result);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {new String[]{}, new JourneyStep[]{}},
            {new String[]{"A"}, new JourneyStep[]{new JourneyStep("A", 1)}},
            {new String[]{"A", "B"}, new JourneyStep[]{new JourneyStep("A", 1),
             new JourneyStep("B", 1)}},
            {new String[]{"A", "A", "A", "B"}, new JourneyStep[]{new JourneyStep("A", 3),
             new JourneyStep("B", 1)}},
            {new String[]{"A", "B", "A", "B"}, new JourneyStep[]{new JourneyStep("A", 1),
             new JourneyStep("B", 1), new JourneyStep("A", 1), new JourneyStep("B", 1)}},
            {new String[]{"A", "A", "A", "A", "A"}, new JourneyStep[]{new JourneyStep("A", 5)}},
        });
    }

}