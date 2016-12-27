package com.github.gcmonitor;

import com.github.gcmonitor.integration.GcMonitorBuilder;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.util.List;

public class GcMonitor implements NotificationListener, AutoCloseable {

    public static GcMonitorBuilder builder() {
        return new GcMonitorBuilder();
    }

    public GcMonitor() {
        this(ManagementFactory.getGarbageCollectorMXBeans());
    }

    public GcMonitor(List<GarbageCollectorMXBean> garbageCollectorMXBeans) {
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            ((NotificationEmitter) bean).addNotificationListener(this, null, bean);
        }
    }

    @Override
    synchronized public void handleNotification(Notification notification, Object handback) {
        Thread thread = Thread.currentThread();
        System.out.println("Handle notification " + notification + " in the thread " + threadToString(thread));
        GarbageCollectorMXBean bean = (GarbageCollectorMXBean) handback;
        long count = bean.getCollectionCount();
        long time = bean.getCollectionTime();
        String collectorName = bean.getName();
        String msg = "collector=" + collectorName +
                ", count=" + count +
                ", time=" + time;
        System.out.println(msg);
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

    private String threadToString(Thread thread) {
        return "id=" + thread.getId() +
                ", name=" + thread.getName() +
                ", daemon=" + thread.isDaemon() +
                ", priority=" + thread.getPriority() +
                ", state=" + thread.getState() +
                ", group=" + thread.getThreadGroup();
    }

}
