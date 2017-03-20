/*
 *  Copyright 2017 Vladimir Bukhtoyarov
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.gcmonitor.example.jmx;

import com.github.gcmonitor.GcMonitor;
import com.github.gcmonitor.example.MemoryConsumer;
import com.github.gcmonitor.integration.jmx.GcMonitorStatistics;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class JmxMain {

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
    public static void main(String[] args) throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException, InterruptedException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        ObjectName consumerName = new ObjectName("com.github.gcmonitor:type=MemoryConsumer");
        MemoryConsumer consumer = new MemoryConsumer();
        server.registerMBean(consumer, consumerName);

        ObjectName monitorName = new ObjectName("com.github.gcmonitor:type=GcMonitor");
        GcMonitor gcMonitor = GcMonitor.builder()
                .addRollingWindow("15min", Duration.ofMinutes(15))
                .build();
        server.registerMBean(new GcMonitorStatistics(gcMonitor), monitorName);

        try {
            while (true) {
                consumer.consume(ThreadLocalRandom.current().nextInt(10) + 1,  1);
                consumer.consume(ThreadLocalRandom.current().nextInt(20) + 100,  1);
                TimeUnit.SECONDS.sleep(5);
            }
        } finally {
            consumer.close();
        }
    }

}
