package com.github.gcmonitor.example.console;

import com.github.gcmonitor.GcMonitor;
import com.github.gcmonitor.example.MemoryConsumer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {

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
    -XX:+CMSScavengeBeforeRemark
     */
    public static void main(String[] args) throws InterruptedException {
        MemoryConsumer consumer = new MemoryConsumer();
        GcMonitor gcMonitor = new GcMonitor();

        try {
            while (true) {
                consumer.consume(ThreadLocalRandom.current().nextInt(10) + 1,  1);
                consumer.consume(ThreadLocalRandom.current().nextInt(20) + 100,  1);
                System.out.println(gcMonitor.getPrettyPrintableStatistics());
                TimeUnit.SECONDS.sleep(5);
            }
        } finally {
            consumer.close();
        }
    }

}
