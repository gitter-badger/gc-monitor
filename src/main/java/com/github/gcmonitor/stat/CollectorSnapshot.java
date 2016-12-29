package com.github.gcmonitor.stat;

import java.lang.management.GarbageCollectorMXBean;

public class CollectorSnapshot {

    private long collectionTimeMillis;
    private long collectionCount;

    public CollectorSnapshot(GarbageCollectorMXBean collectorMbean) {
        this.collectionCount = collectorMbean.getCollectionCount();
        this.collectionTimeMillis = collectorMbean.getCollectionTime();
    }

    public void update(long collectionTimeMillis, long collectionCount) {
        this.collectionTimeMillis = collectionTimeMillis;
        this.collectionCount = collectionCount;
    }

    public long getCollectionCount() {
        return collectionCount;
    }

    public long getCollectionTimeMillis() {
        return collectionTimeMillis;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CollectorSnapshot{");
        sb.append("collectionTimeMillis=").append(collectionTimeMillis);
        sb.append(", collectionCount=").append(collectionCount);
        sb.append('}');
        return sb.toString();
    }

}
