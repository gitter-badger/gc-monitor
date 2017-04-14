/*
 *  Copyright 2017 Vladimir Bukhtoyarov
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.gcmonitor;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.time.Duration;

import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class GcMonitorBuilderTest {

    private static abstract class GcMonitorBuilderTestBase {

        GcMonitorBuilder builder = GcMonitor.builder();
    }

    public static class PercentilesValidation extends GcMonitorBuilderTestBase {

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowNegativePercentile() {
            builder.withPercentiles(new double[] {0.5, -0.01});
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowPercentileGreateThan1() {
            builder.withPercentiles(new double[] {0.5, 1.01});
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowDuplicates() {
            builder.withPercentiles(new double[] {0.5, 0.7, 0.5});
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowEmptyPercentiles() {
            builder.withPercentiles(new double[0]);
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowNullPercentiles() {
            builder.withPercentiles(null);
        }

    }

    public static class ClockValidation extends GcMonitorBuilderTestBase {

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowNullClock() {
            builder.withClock(null);
        }

    }

    public static class RollingWindowValidation extends GcMonitorBuilderTestBase {

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowNegativeWindow() {
            builder.addRollingWindow("my-window", Duration.ofMinutes(-1));
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowZeroWindow() {
            builder.addRollingWindow("my-window", Duration.ofMinutes(0));
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowNullWindow() {
            builder.addRollingWindow("my-window", null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowNullWindowName() {
            builder.addRollingWindow(null, Duration.ofMinutes(1));
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldDisallowDuplicatesOfWindowName() {
            builder.addRollingWindow("my-window", Duration.ofMinutes(1));
            builder.addRollingWindow("my-window", Duration.ofMinutes(90));
        }

    }


}