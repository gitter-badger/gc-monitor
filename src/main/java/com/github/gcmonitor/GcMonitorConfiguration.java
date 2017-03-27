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

import com.github.gcmonitor.stat.WindowSpecification;
import com.github.rollingmetrics.util.Clock;

import java.lang.management.GarbageCollectorMXBean;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GcMonitorConfiguration {

    public static final String AGGREGATED_COLLECTOR_NAME = "AggregatedCollector";
    public static final String UNIFORM_WINDOW_NAME = "uniform";
    public static final int MAX_WINDOWS = 20;
    public static final int COUNTER_CHUNKS = 10;
    public static final int HISTOGRAM_CHUNKS = 5;
    public static final long LONGEST_TRACKABLE_PAUSE_MILLIS = TimeUnit.MINUTES.toMillis(15);
    public static int DECIMAL_POINTS = 3;

    private final List<GarbageCollectorMXBean> garbageCollectorMXBeans;
    private final SortedSet<String> collectorNames;
    private final SortedMap<String, WindowSpecification> windowSpecifications;
    private final double[] percentiles;
    private final boolean aggregateDifferentCollectors;
    private final Clock clock;

    public GcMonitorConfiguration(SortedMap<String, WindowSpecification> windowSpecifications, double[] percentiles, List<GarbageCollectorMXBean> garbageCollectorMXBeans, boolean aggregateDifferentCollectors, Clock clock) {
        this.windowSpecifications = Collections.unmodifiableSortedMap(windowSpecifications);
        this.garbageCollectorMXBeans = Collections.unmodifiableList(garbageCollectorMXBeans);
        this.percentiles = percentiles.clone();
        this.aggregateDifferentCollectors = aggregateDifferentCollectors;
        this.clock = clock;

        this.collectorNames = new TreeSet<>();
        garbageCollectorMXBeans.forEach(bean -> collectorNames.add(bean.getName()));
        if (aggregateDifferentCollectors && garbageCollectorMXBeans.size() > 1) {
            collectorNames.add(AGGREGATED_COLLECTOR_NAME);
        }
    }

    public List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
        return garbageCollectorMXBeans;
    }

    public SortedMap<String, WindowSpecification> getWindowSpecifications() {
        return windowSpecifications;
    }

    public Set<String> getWindowNames() {
        return windowSpecifications.keySet();
    }

    public SortedSet<String> getCollectorNames() {
        return collectorNames;
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

    public boolean isAggregateDifferentCollectors() {
        return aggregateDifferentCollectors;
    }
}
