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

import com.github.gcmonitor.stat.*;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.GarbageCollectorMXBean;
import java.util.function.Consumer;

public class GcMonitor implements NotificationListener {

    private final GcMonitorConfiguration configuration;

    private GcStatistics statistics;

    public static GcMonitorBuilder builder() {
        return new GcMonitorBuilder();
    }

    GcMonitor(GcMonitorConfiguration configuration) {
        this.configuration = configuration;
    }

    synchronized public GcMonitorSnapshot getSnapshot() {
        if (statistics == null) {
            return GcStatistics.createEmptySnapshot(configuration);
        }
        return statistics.getSnapshot();
    }

    @Override
    synchronized public void handleNotification(Notification notification, Object handback) {
        String collectorName = (String) handback;
        statistics.handleNotification(collectorName, notification);
    }

    public synchronized GcMonitorConfiguration getConfiguration() {
        return configuration;
    }

    public synchronized void start() {
        if (statistics != null) {
            // already started
            return;
        }

        this.statistics = GcStatistics.create(configuration);

        for (GarbageCollectorMXBean bean : configuration.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) bean;
            emitter.addNotificationListener(this, null, bean.getName());
        }
    }

    public synchronized void stop() {
        if (statistics == null) {
            return;
        }

        statistics = null;
        for (GarbageCollectorMXBean bean : configuration.getGarbageCollectorMXBeans()) {
            try {
                ((NotificationEmitter) bean).removeNotificationListener(this);
            } catch (ListenerNotFoundException e) {
                // Do nothing
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GcMonitor{");
        sb.append("configuration=").append(configuration);
        sb.append(", snapshot=").append(getSnapshot());
        sb.append('}');
        return sb.toString();
    }

    synchronized public String getPrettyPrintableStatistics() {
        final StringBuilder sb = new StringBuilder("GcMonitor{");
        if (statistics != null) {
            statistics.printItself(sb, "\t\t");
        } else {
            sb.append("GC event listening is not started.");
        }
        sb.append("\n}");
        return sb.toString();
    }

}
