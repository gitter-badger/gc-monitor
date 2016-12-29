package com.github.gcmonitor.stat;

import javax.management.Notification;
import java.lang.management.GarbageCollectorMXBean;

/**
 * Created by vladimir.bukhtoyarov on 28.12.2016.
 */
public final class MonitoredCollector {

    private final GarbageCollectorMXBean collectorMbean;
    private final CollectorStatistics collectorStat;
    private final CollectorStatistics globalStat;
    private final CollectorStatistics[] allStats;
    private final CollectorSnapshot snapshot;

    public MonitoredCollector(GarbageCollectorMXBean collectorMbean, CollectorStatistics globalStat, long[] timeWindows, double[] percentiles) {
        this.collectorMbean = collectorMbean;
        this.collectorStat = new CollectorStatistics(timeWindows, percentiles);
        this.globalStat = globalStat;
        this.allStats = new CollectorStatistics[] {globalStat, collectorStat};
        this.snapshot = new CollectorSnapshot(collectorMbean);
    }

    public CollectorStatistics getCollectorStat() {
        return collectorStat;
    }

    public void handleNotification(Notification notification) {
        long collectionTimeMillis = collectorMbean.getCollectionTime();
        long collectionCount = collectorMbean.getCollectionCount();

        long collectionTimeDeltaMillis = collectionTimeMillis - snapshot.collectionTimeMillis;
        long collectionCountDelta = collectionCount - snapshot.collectionTimeMillis;
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
