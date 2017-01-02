package com.github.gcmonitor.example.jmx;

import com.github.gcmonitor.GcMonitor;
import com.github.gcmonitor.example.MemoryConsumer;
import com.github.gcmonitor.integration.jmx.GcMonitorStatistics;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class JmxMain {

    public static void main(String[] args) throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException, InterruptedException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        ObjectName consumerName = new ObjectName("com.github.gcmonitor:type=MemoryConsumer");
        server.registerMBean(new MemoryConsumer(), consumerName);

        ObjectName monitorName = new ObjectName("com.github.gcmonitor:type=GcMonitor");
        GcMonitor gcMonitor = new GcMonitor();
        server.registerMBean(new GcMonitorStatistics(gcMonitor), monitorName);

        Thread.currentThread().join();
    }

}
