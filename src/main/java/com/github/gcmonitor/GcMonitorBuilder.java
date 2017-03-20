
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

package com.github.gcmonitor;

import com.github.gcmonitor.stat.GcMonitorConfiguration;
import com.github.gcmonitor.stat.WindowSpecification;
import com.github.rollingmetrics.util.Clock;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;


public class GcMonitorBuilder {

    public static final double[] DEFAULT_PERCENTILES = new double[] {0.5, 0.75, 0.9, 0.95, 0.98, 0.99, 0.999};

    private final SortedMap<String, WindowSpecification> windows;
    private List<GarbageCollectorMXBean> garbageCollectorMXBeans;
    private double[] percentiles = DEFAULT_PERCENTILES;
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
        this.percentiles = percentiles;
        return this;
    }

    public GcMonitorBuilder addRollingWindow(String windowName, Duration rollingWindow) {
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
        this.clock = Objects.requireNonNull(clock);
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

}