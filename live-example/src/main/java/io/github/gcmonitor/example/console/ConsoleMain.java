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

package io.github.gcmonitor.example.console;

import io.github.gcmonitor.GcMonitor;
import io.github.gcmonitor.example.MemoryConsumer;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ConsoleMain {

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
        GcMonitor gcMonitor = GcMonitor.builder()
                .addRollingWindow("15min", Duration.ofMinutes(15))
                .build();
        gcMonitor.start();

        try {
            while (true) {
                consumer.consume(ThreadLocalRandom.current().nextInt(10) + 1,  1);
                consumer.consume(ThreadLocalRandom.current().nextInt(20) + 100,  1);
                System.out.println(gcMonitor.getSnapshot());
                TimeUnit.SECONDS.sleep(5);
            }
        } finally {
            consumer.close();
            gcMonitor.stop();
        }
    }

}
