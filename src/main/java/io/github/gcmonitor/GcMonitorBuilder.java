
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

import io.github.gcmonitor.stat.WindowSpecification;
import com.github.rollingmetrics.util.Clock;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.*;


public class GcMonitorBuilder {

    private final SortedMap<String, WindowSpecification> windows;
    private List<GarbageCollectorMXBean> garbageCollectorMXBeans;
    private double[] percentiles = GcMonitorConfiguration.DEFAULT_PERCENTILES;
    private boolean aggregateStatFromDifferentCollectors = true;
    private Clock clock = Clock.DEFAULT_CLOCK;

    public GcMonitor build() {
        return new GcMonitor(createConfiguration());
    }

    GcMonitorBuilder() {
        this.windows = new TreeMap<>();
        this.windows.put(GcMonitorConfiguration.UNIFORM_WINDOW_NAME, WindowSpecification.uniform());
        this.garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    }

    public GcMonitorBuilder withPercentiles(double[] percentiles) {
        validatePerventiles(percentiles);
        this.percentiles = percentiles.clone();
        Arrays.sort(this.percentiles);
        return this;
    }

    public GcMonitorBuilder addRollingWindow(String windowName, Duration rollingWindow) {
        validateRollingWindow(windowName, rollingWindow);
        windows.put(windowName, WindowSpecification.rollingWindow(rollingWindow));
        return this;
    }

    public GcMonitorBuilder withoutUniformWindow() {
        windows.remove(GcMonitorConfiguration.UNIFORM_WINDOW_NAME);
        return this;
    }

    public GcMonitorBuilder withGarbageCollectorMXBeans(List<GarbageCollectorMXBean> garbageCollectorMXBeans) {
        checkMbeansCount(garbageCollectorMXBeans);
        this.garbageCollectorMXBeans = garbageCollectorMXBeans;
        return this;
    }

    public GcMonitorBuilder withClock(Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("clock should not be null");
        }
        this.clock = clock;
        return this;
    }

    public GcMonitorBuilder aggregateStatFromDifferentCollectors(boolean aggregateStatFromDifferentCollectors) {
        this.aggregateStatFromDifferentCollectors = aggregateStatFromDifferentCollectors;
        return this;
    }

    GcMonitorConfiguration createConfiguration() {
        checkMbeansCount(garbageCollectorMXBeans);
        return new GcMonitorConfiguration(windows, percentiles, garbageCollectorMXBeans, aggregateStatFromDifferentCollectors, clock);
    }

    private void checkMbeansCount(List<GarbageCollectorMXBean> garbageCollectorMXBeans) {
        if (garbageCollectorMXBeans.isEmpty()) {
            throw new IllegalArgumentException("There are no one GarbageCollectorMXBean in the JVM");
        }
    }

    private void validatePerventiles(double[] percentiles) {
        if (percentiles == null) {
            throw new IllegalArgumentException("percentiles array should not be null");
        }
        if (percentiles.length == 0) {
            throw new IllegalArgumentException("percentiles array should not be empty");
        }

        for (int i = 0; i < percentiles.length; i++) {
            if (percentiles[i] <= 0.0 || percentiles[i] > 1.0) {
                String msg = "Wrong percentile " + percentiles[i] + " at index " + i + ", percentile should be between 0 and 1";
                throw new IllegalArgumentException(msg);
            }
            for (int j = i + 1; j < percentiles.length; j++) {
                if (percentiles[i] == percentiles[j]) {
                    String msg = "percentile " + percentiles[i] + " has been duplicated at positions [" + i + "," + j + "]";
                    throw new IllegalArgumentException(msg);
                }
            }
        }
    }

    private void validateRollingWindow(String windowName, Duration rollingWindow) {
        if (windowName == null) {
            throw new IllegalArgumentException("windowName should not be null");
        }
        if (windows.containsKey(windowName)) {
            throw new IllegalArgumentException("Window with name " + windowName + " already configured");
        }
        if (rollingWindow == null) {
            throw new IllegalArgumentException("rollingWindow should not be null");
        }
        if (rollingWindow.isZero() || rollingWindow.isNegative()) {
            throw new IllegalArgumentException("rollingWindow should be a positive duration");
        }
    }

}