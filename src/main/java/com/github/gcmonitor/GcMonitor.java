package com.github.gcmonitor;

import com.github.gcmonitor.stat.CollectorStatistics;
import com.github.gcmonitor.stat.MonitoredCollector;
import com.github.gcmonitor.stat.PrettyPrintable;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Consumer;

public class GcMonitor implements NotificationListener, AutoCloseable {

    public static final String AGGREGATED_COLLECTOR_NAME = "Aggregated-Collector";

    private final Map<String, CollectorStatistics> statistics;
    private final GcMonitorConfiguration configuration;

    public GcMonitor() {
        this(GcMonitorConfiguration.DEFAULT);
    }

    public GcMonitor(GcMonitorConfiguration configuration) {
        this(configuration, ManagementFactory.getGarbageCollectorMXBeans());
    }

    GcMonitor(GcMonitorConfiguration configuration, List<GarbageCollectorMXBean> garbageCollectorMXBeans) {
        if (garbageCollectorMXBeans.isEmpty()) {
            throw new IllegalArgumentException("There are no one GarbageCollectorMXBean in the JVM");
        }
        this.configuration = configuration;

        statistics = new HashMap<>();
        Optional<CollectorStatistics> aggregatedStatistics;
        if (garbageCollectorMXBeans.size() > 1) {
            aggregatedStatistics = Optional.of(new CollectorStatistics(configuration));
            statistics.put(AGGREGATED_COLLECTOR_NAME, aggregatedStatistics.get());
        } else {
            aggregatedStatistics = Optional.empty();
        }

        for (GarbageCollectorMXBean bean : garbageCollectorMXBeans) {
            NotificationEmitter emitter = (NotificationEmitter) bean;
            MonitoredCollector handback = new MonitoredCollector(bean, aggregatedStatistics, configuration);
            statistics.put(bean.getName(), handback.getCollectorStatistics());
            emitter.addNotificationListener(this, null, handback);
        }
    }


    synchronized public String getPrettyPrintableStatistics() {
        final StringBuilder sb = new StringBuilder("GcMonitor{");
        statistics.forEach((collectorName, stat) -> {
            sb.append("\n\t").append(collectorName).append("=\n");
            stat.printItself(sb, "\t\t");
        });
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GcMonitor{");
        sb.append("statistics=").append(statistics);
        sb.append(", configuration=").append(configuration);
        sb.append('}');
        return sb.toString();
    }

    synchronized public void getStatistics(Consumer<Map<String, CollectorStatistics>> consumer) {
        consumer.accept(statistics);
    }

    @Override
    synchronized public void handleNotification(Notification notification, Object handback) {
        MonitoredCollector handler = (MonitoredCollector) handback;
        handler.handleNotification(notification);
    }

    public GcMonitorConfiguration getConfiguration() {
        return configuration;
    }

    public Set<String> getCollectorNames() {
        return statistics.keySet();
    }

    @Override
    public void close() {
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            try {
                ((NotificationEmitter) bean).removeNotificationListener(this);
            } catch (ListenerNotFoundException e) {
                // Do nothing
            }
        }
    }

}
