package com.github.gcmonitor.example;

public interface MemoryConsumerMBean {

    void consume(int megabytes, int seconds);

}
