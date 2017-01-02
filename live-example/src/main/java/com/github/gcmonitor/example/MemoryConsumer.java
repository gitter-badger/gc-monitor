package com.github.gcmonitor.example;

import com.github.rollingmetrics.util.DaemonThreadFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryConsumer implements AutoCloseable, MemoryConsumerMBean {

    private final ScheduledExecutorService scheduler;
    private ConcurrentHashMap<Long, byte[]> memory = new ConcurrentHashMap<>();
    private AtomicLong sequence = new AtomicLong();

    public MemoryConsumer() {
        DaemonThreadFactory daemonThreadFactory = new DaemonThreadFactory("memory-cleaner-scheduler");
        this.scheduler = Executors.newScheduledThreadPool(1, daemonThreadFactory);
    }

    @Override
    public void close() {
        scheduler.shutdown();
    }

    @Override
    public void consume(int megabytes, int seconds) {
        long key = sequence.incrementAndGet();
        byte[] bytes = new byte[1_000_000 * megabytes];

        memory.put(key, bytes);
        scheduler.schedule(() -> memory.remove(key), seconds, TimeUnit.SECONDS);
    }

}
