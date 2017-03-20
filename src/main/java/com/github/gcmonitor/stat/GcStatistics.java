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

import com.sun.java.swing.plaf.windows.resources.windows;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GcStatistics implements PrettyPrintable {

    private final Map<String, MonitoredCollector> monitoredCollectors;
    private final Map<String, CollectorStatistics> perCollectorStatistics;

    public static GcStatistics create(GcMonitorConfiguration configuration) {
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = configuration.getGarbageCollectorMXBeans();

        long[] timeWindows = configuration.getTimeWindowSpecifications();
        this.windows = new CollectorWindow[timeWindows.length];
        for (int i = 0; i < windows.length; i++) {
            windows[i] = new CollectorWindow(timeWindows[i], configuration);
        }

        Optional<CollectorStatistics> aggregatedStatistics;
        if (garbageCollectorMXBeans.size() > 1) {
            aggregatedStatistics = Optional.of(new CollectorStatistics(configuration));
            perCollectorStatistics.put(GcMonitorConfiguration.AGGREGATED_COLLECTOR_NAME, aggregatedStatistics.get());
        } else {
            aggregatedStatistics = Optional.empty();
        }

        for (GarbageCollectorMXBean bean : configuration.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) bean;
            MonitoredCollector handback = new MonitoredCollector(bean, aggregatedStatistics, configuration);
            perCollectorStatistics.put(bean.getName(), handback.getCollectorStatistics());
            emitter.addNotificationListener(this, null, handback);
        }
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
