package com.github.gcmonitor.stat;

import com.github.gcmonitor.GcMonitorConfiguration;

import javax.management.Notification;
import java.lang.management.GarbageCollectorMXBean;
import java.util.Optional;

public final class MonitoredCollector {

    private final GarbageCollectorMXBean collectorMbean;
    private final CollectorStatistics collectorStatistics;
    private final CollectorStatistics[] allStats;
    private final CollectorSnapshot snapshot;

    public MonitoredCollector(GarbageCollectorMXBean collectorMbean, Optional<CollectorStatistics> aggregatedStatistics, GcMonitorConfiguration configuration) {
        this.collectorMbean = collectorMbean;
        this.collectorStatistics = new CollectorStatistics(configuration);
        if (aggregatedStatistics.isPresent()) {
            this.allStats = new CollectorStatistics[] {aggregatedStatistics.get(), collectorStatistics};
        } else {
            this.allStats = new CollectorStatistics[] {collectorStatistics};
        }
        this.snapshot = new CollectorSnapshot(collectorMbean);
    }

    public CollectorStatistics getCollectorStatistics() {
        return collectorStatistics;
    }

    public void handleNotification(Notification notification) {
        long collectionTimeMillis = collectorMbean.getCollectionTime();
        long collectionCount = collectorMbean.getCollectionCount();

        long collectionTimeDeltaMillis = collectionTimeMillis - snapshot.getCollectionTimeMillis();
        long collectionCountDelta = collectionCount - snapshot.getCollectionCount();
        if (collectionCountDelta == 0) {
            return;
        }
        long averageTimeMillis = collectionTimeDeltaMillis / collectionCountDelta;

        for (CollectorStatistics stat : allStats) {
            for (CollectorStatisticsWindow window : stat.getWindows()) {
                window.getCounter().add(collectionTimeDeltaMillis);
                for (int i = 0; i < collectionCountDelta; i++) {
                    window.getHistogram().update(averageTimeMillis);
                }
            }
        }
        snapshot.update(collectionTimeMillis, collectionCount);
    }

}
