package com.github.gcmonitor;

import com.github.gcmonitor.stat.CollectorStatistics;
import com.github.gcmonitor.stat.MonitoredCollector;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GcMonitor implements NotificationListener, AutoCloseable {

    public static final String AGGREGATED_COLLECTOR_NAME = "Aggregated";
    public static final int MAX_WINDOWS = 20;

    public static final long[] DEFAULT_TIME_WINDOWS = {
            TimeUnit.MINUTES.toSeconds(1),
            TimeUnit.MINUTES.toSeconds(5),
            TimeUnit.MINUTES.toSeconds(15),
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.HOURS.toSeconds(24),
    };

    public static final int COUNTER_CHUNKS = 10;
    public static final int HISTOGRAM_CHUNKS = 5;
    public static final long LONGEST_TRACKABLE_PAUSE_MILLIS = TimeUnit.MINUTES.toMillis(15);

    public GcMonitor() {
        this(DEFAULT_TIME_WINDOWS);
    }

    private final Map<String, CollectorStatistics> statistics;

    public GcMonitor(long[] timeWindows) {
        this(timeWindows, ManagementFactory.getGarbageCollectorMXBeans());
    }

    GcMonitor(long[] timeWindows, List<GarbageCollectorMXBean> garbageCollectorMXBeans) {
        validateTimeWindows(timeWindows);
        statistics = new HashMap<>();
        CollectorStatistics globalStat = new CollectorStatistics(timeWindows);
        statistics.put(AGGREGATED_COLLECTOR_NAME, globalStat);

        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) bean;
            MonitoredCollector handback = new MonitoredCollector(bean, globalStat, timeWindows);
            statistics.put(bean.getName(), handback.getCollectorStat());
            emitter.addNotificationListener(this, null, handback);
        }
    }

    private static void validateTimeWindows(long[] timeWindows) {
        // TODO
    }

    public Map<String, CollectorStatistics> getStatistics() {
        return statistics;
    }

    @Override
    synchronized public void handleNotification(Notification notification, Object handback) {
        MonitoredCollector handler = (MonitoredCollector) handback;
        handler.handleNotification(notification);
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
