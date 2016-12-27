package com.github.gcmonitor.example.console;

import com.github.gcmonitor.GcMonitor;
import com.github.gcmonitor.example.MemoryConsumer;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        MemoryConsumer consumer = new MemoryConsumer();
        GcMonitor gcMonitor = new GcMonitor();

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("------------------------------------------------");
            for (final MemoryPoolMXBean pool : memoryPoolMXBeans) {
                MemoryUsage usage = pool.getUsage();
                System.out.println(pool.getName() + ": " + usage);
            }

            System.out.println();
            System.out.println("enter megabytes: ");
            int megabytes = scanner.nextInt();

            System.out.println("enter seconds: ");
            int seconds = scanner.nextInt();
            consumer.consume(megabytes, seconds);
        }
    }

}
