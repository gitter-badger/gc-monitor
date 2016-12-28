package com.github.gcmonitor.stat;

import java.lang.management.GarbageCollectorMXBean;

/**
 * Created by vladimir.bukhtoyarov on 28.12.2016.
 */
public class CollectorSnapshot {

    long collectionTimeMillis;
    long collectionCount;

    public CollectorSnapshot(GarbageCollectorMXBean collectorMbean) {
        this.collectionCount = collectorMbean.getCollectionCount();
        this.collectionTimeMillis = collectorMbean.getCollectionTime();
    }

    void update(long collectionTimeMillis, long collectionCount) {
        this.collectionTimeMillis = collectionTimeMillis;
        this.collectionCount = collectionCount;
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
