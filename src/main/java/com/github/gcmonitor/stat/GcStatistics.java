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

import javax.management.Notification;
import java.lang.management.GarbageCollectorMXBean;
import java.util.*;

public class GcStatistics implements PrettyPrintable {

    private final SortedMap<String, MonitoredCollector> monitoredCollectors;
    private final SortedMap<String, CollectorStatistics> perCollectorStatistics;

    public static GcStatistics create(GcMonitorConfiguration configuration) {
        SortedMap<String, MonitoredCollector> monitoredCollectors = new TreeMap<>();
        SortedMap<String, CollectorStatistics> perCollectorStatistics = new TreeMap<>();
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = configuration.getGarbageCollectorMXBeans();
        long currentTimeMillis = configuration.getClock().currentTimeMillis();

        Optional<CollectorStatistics> aggregatedStatistics;
        if (garbageCollectorMXBeans.size() > 1 && configuration.isAggregateStatFromDifferentCollectors()) {
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
        return new GcStatistics(monitoredCollectors, perCollectorStatistics);
    }

    private static CollectorStatistics createCollectorStatistics(GcMonitorConfiguration configuration, long currentTimeMillis) {
        SortedMap<String, CollectorWindow> windows = new TreeMap<>();
        configuration.getWindowSpecifications().forEach((windowName, windowSpec) -> {
            CollectorWindow window = windowSpec.createWindow(currentTimeMillis, configuration);
            windows.put(windowName, window);
        });
        return new CollectorStatistics(windows);
    }

    private GcStatistics(SortedMap<String, MonitoredCollector> monitoredCollectors, SortedMap<String, CollectorStatistics> perCollectorStatistics) {
        this.monitoredCollectors = monitoredCollectors;
        this.perCollectorStatistics = perCollectorStatistics;
    }

    public void handleNotification(String collectorName, Notification notification) {
        MonitoredCollector monitoredCollector = monitoredCollectors.get(collectorName);
        monitoredCollector.handleNotification(notification);
    }

    @Override
    public void printItself(StringBuilder builder, String indent) {
        perCollectorStatistics.forEach((collectorName, statistics) -> {
            builder.append("\n\t").append(collectorName).append("=\n");
            statistics.printItself(builder, indent);
        });
    }

}
