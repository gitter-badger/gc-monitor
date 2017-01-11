package com.github.gcmonitor.example.console;

import com.github.rollingmetrics.util.DaemonThreadFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by vladimir.bukhtoyarov on 11.01.2017.
 */
public class MemoryConsumer {

    private final ScheduledExecutorService scheduler;
    private ConcurrentHashMap<byte[], byte[]> memory = new ConcurrentHashMap<>();

    MemoryConsumer() {
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    void shutdown() {
        scheduler.shutdown();
    }

    void consume(int megabytesInEden, int megabytesInTenured, int seconds) {
        byte[] newBytes = new byte[1_000_000 * megabytesInEden];
        byte[] oldBytes = new byte[1_000_000 * megabytesInTenured];

        memory.put(newBytes, oldBytes);
        scheduler.schedule(() -> memory.remove(newBytes), seconds, TimeUnit.SECONDS);
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
    -XX:+CMSScavengeBeforeRemark
    -verbose:gc
    -XX:+PrintGC
    -XX:+PrintGCDetails
    -XX:+PrintGCTimeStamps
    -XX:+PrintGCDateStamps
    -XX:+PrintTenuringDistribution
     */
    public static void main(String[] args) throws InterruptedException {
        long startMillis = System.currentTimeMillis();
        MemoryConsumer consumer = new MemoryConsumer();
        try {
            while (true) {
                long secondsSinceStart = (System.currentTimeMillis() - startMillis) / 1000;
                System.out.println(secondsSinceStart + " seconds since start");
                consumer.consume(1, 100, 1);
                TimeUnit.SECONDS.sleep(10);
            }
        } finally {
            consumer.shutdown();
        }
    }

}
