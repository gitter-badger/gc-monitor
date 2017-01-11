package com.github.gcmonitor.example.console;

import com.github.rollingmetrics.util.DaemonThreadFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vladimir.bukhtoyarov on 11.01.2017.
 */
public class MemoryConsumer {

    private final ScheduledExecutorService scheduler;
    private ConcurrentHashMap<Long, byte[]> memory = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    MemoryConsumer() {
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    void shutdown() {
        scheduler.shutdown();
    }

    void consume(int megabytes, int seconds) {
        byte[] oldBytes = new byte[1_000_000 * megabytes];

        Long chunkId = sequence.incrementAndGet();
        memory.put(chunkId, oldBytes);
        scheduler.schedule(() -> memory.remove(chunkId), seconds, TimeUnit.SECONDS);
    }

    /*
    -Xmx1024m
    -Xms1024m
    -XX:+UseParNewGC
    -XX:+UseConcMarkSweepGC
    -XX:NewSize=64m
    -XX:MaxNewSize=64m
    -XX:+UseCMSInitiatingOccupancyOnly
    -XX:CMSInitiatingOccupancyFraction=50
    -verbose:gc
    -XX:+PrintGC
    -XX:+PrintGCDetails
    -XX:+PrintGCTimeStamps
    -XX:+PrintGCDateStamps
    -XX:+PrintTenuringDistribution

    then add -XX:+CMSScavengeBeforeRemark
     */
    public static void main(String[] args) throws InterruptedException {
        long startMillis = System.currentTimeMillis();
        MemoryConsumer consumer = new MemoryConsumer();
        try {
            while (true) {
                long secondsSinceStart = (System.currentTimeMillis() - startMillis) / 1000;
                System.out.println(secondsSinceStart + " seconds since start");
                consumer.consume(100,  1);
                TimeUnit.SECONDS.sleep(5);
            }
        } finally {
            consumer.shutdown();
        }
    }

}
