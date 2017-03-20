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

package com.github.gcmonitor.stat;

import com.github.rollingmetrics.util.Clock;

import java.lang.management.GarbageCollectorMXBean;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GcMonitorConfiguration {

    public static final String AGGREGATED_COLLECTOR_NAME = "Aggregated-Collector";
    public static final String UNIFORM_WINDOW_NAME = "uniform";
    public static final int MAX_WINDOWS = 20;
    public static final int COUNTER_CHUNKS = 10;
    public static final int HISTOGRAM_CHUNKS = 5;
    public static final long LONGEST_TRACKABLE_PAUSE_MILLIS = TimeUnit.MINUTES.toMillis(15);
    public static int DECIMAL_POINTS = 3;

    private final List<GarbageCollectorMXBean> garbageCollectorMXBeans;
    private final SortedMap<String, WindowSpecification> timeWindowSpecifications;
    private final double[] percentiles;
    private final Clock clock;

    public GcMonitorConfiguration(Map<String, WindowSpecification> timeWindowSpecifications, double[] percentiles, List<GarbageCollectorMXBean> garbageCollectorMXBeans, Clock clock) {
        this.timeWindowSpecifications = new TreeMap<>();
        this.garbageCollectorMXBeans = garbageCollectorMXBeans;
        this.percentiles = percentiles.clone();
        this.clock = clock;
    }

    public List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
        return garbageCollectorMXBeans;
    }

    public SortedMap<String, WindowSpecification> getTimeWindowSpecifications() {
        return timeWindowSpecifications;
    }

    public Clock getClock() {
        return clock;
    }

    public double[] getPercentiles() {
        return percentiles;
    }

    public int getCounterChunks() {
        return COUNTER_CHUNKS;
    }

    public int getHistogramChunks() {
        return HISTOGRAM_CHUNKS;
    }

    public long getLongestTrackablePauseMillis() {
        return LONGEST_TRACKABLE_PAUSE_MILLIS;
    }

    public int getDecimalPoints() {
        return DECIMAL_POINTS = 2;
    }

}
