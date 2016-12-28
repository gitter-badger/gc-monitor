package com.github.gcmonitor.integration.jmx;

import com.github.gcmonitor.GcMonitor;
import com.github.gcmonitor.integration.jmx.data.GcMonitorData;
import com.github.gcmonitor.integration.jmx.data.type.GcMonitorDataType;

import javax.management.openmbean.CompositeType;

public class GcMonitorImpl implements GcMonitorMXBean {

    private final GcMonitor gcMonitor;
    private final GcMonitorDataType type;

    public GcMonitorImpl(GcMonitor gcMonitor) {
        this.gcMonitor = gcMonitor;
        this.type = GcMonitorDataType.buildCompositeType(gcMonitor.getStatistics());
    }

    @Override
    public GcMonitorData getGcMonitorData() {
        return new GcMonitorData(type, gcMonitor.getStatistics());
    }

}
