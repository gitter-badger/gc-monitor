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

import com.github.rollingmetrics.histogram.util.EmptySnapshot;

import javax.management.Notification;
import java.lang.management.GarbageCollectorMXBean;
import java.util.*;

public class GcStatistics {

    private final GcMonitorConfiguration configuration;
    private final SortedMap<String, MonitoredCollector> monitoredCollectors;
    private final SortedMap<String, CollectorStatistics> perCollectorStatistics;

    public static GcStatistics create(GcMonitorConfiguration configuration) {
        SortedMap<String, MonitoredCollector> monitoredCollectors = new TreeMap<>();
        SortedMap<String, CollectorStatistics> perCollectorStatistics = new TreeMap<>();
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = configuration.getGarbageCollectorMXBeans();
        long currentTimeMillis = configuration.getClock().currentTimeMillis();

        Optional<CollectorStatistics> aggregatedStatistics;
        if (garbageCollectorMXBeans.size() > 1 && configuration.isAggregateDifferentCollectors()) {
            aggregatedStatistics = Optional.of(createCollectorStatistics(configuration, currentTimeMillis));
            perCollectorStatistics.put(GcMonitorConfiguration.AGGREGATED_COLLECTOR_NAME, aggregatedStatistics.get());
        } else {
            aggregatedStatistics = Optional.empty();
        }

        for (GarbageCollectorMXBean bean : configuration.getGarbageCollectorMXBeans()) {
            CollectorStatistics collectorStatistics = createCollectorStatistics(configuration, currentTimeMillis);
            perCollectorStatistics.put(bean.getName(), collectorStatistics);
            MonitoredCollector monitoredCollector = new MonitoredCollector(bean, aggregatedStatistics, collectorStatistics);
            monitoredCollectors.put(bean.getName(), monitoredCollector);
        }
        return new GcStatistics(configuration, monitoredCollectors, perCollectorStatistics);
    }

    private static CollectorStatistics createCollectorStatistics(GcMonitorConfiguration configuration, long currentTimeMillis) {
        SortedMap<String, CollectorWindow> windows = new TreeMap<>();
        configuration.getWindowSpecifications().forEach((windowName, windowSpec) -> {
            CollectorWindow window = windowSpec.createWindow(currentTimeMillis, configuration);
            windows.put(windowName, window);
        });
        return new CollectorStatistics(windows);
    }

    private GcStatistics(GcMonitorConfiguration configuration, SortedMap<String, MonitoredCollector> monitoredCollectors, SortedMap<String, CollectorStatistics> perCollectorStatistics) {
        this.configuration = configuration;
        this.monitoredCollectors = monitoredCollectors;
        this.perCollectorStatistics = perCollectorStatistics;
    }

    public void handleNotification(String collectorName, Notification notification) {
        MonitoredCollector monitoredCollector = monitoredCollectors.get(collectorName);
        monitoredCollector.handleNotification(notification);
    }

    public GcMonitorSnapshot getSnapshot() {
        long currentTimeMillis = configuration.getClock().currentTimeMillis();

        Map<String, Map<String, CollectorWindowSnapshot>> perCollectorSnapshots = new HashMap<>();
        perCollectorStatistics.forEach((collectorName, collectorStatistics) -> {
            Map<String, CollectorWindowSnapshot> collectorMap = new HashMap<>();
            perCollectorSnapshots.put(collectorName, collectorMap);
            collectorStatistics.getWindows().forEach((windowName, window) -> {
                CollectorWindowSnapshot windowSnapshot = window.getSnapshot(currentTimeMillis);
                collectorMap.put(windowName, windowSnapshot);
            });
        });

        return new GcMonitorSnapshot(perCollectorSnapshots);
    }

    public static GcMonitorSnapshot createEmptySnapshot(GcMonitorConfiguration configuration) {
        Map<String, Map<String, CollectorWindowSnapshot>> perCollectorSnapshots = new HashMap<>();
        for (String collectorName : configuration.getCollectorNames()) {
            Map<String, CollectorWindowSnapshot> collectorMap = new HashMap<>();
            perCollectorSnapshots.put(collectorName, collectorMap);
            for (String windowName : configuration.getWindowNames()) {
                CollectorWindowSnapshot fakeSnapshot = new CollectorWindowSnapshot(EmptySnapshot.INSTANCE, 0L, 0.0d);
                collectorMap.put(windowName, fakeSnapshot);
            }
        }

        return new GcMonitorSnapshot(perCollectorSnapshots);
    }

}
