package com.github.gcmonitor.integration.jmx;

import com.github.gcmonitor.integration.jmx.data.GcMonitorData;

public interface GcMonitorMXBean {

    GcMonitorData getGcMonitorData();

}
