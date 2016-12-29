package com.github.gcmonitor.integration.jmx;

import com.github.gcmonitor.GcMonitor;
import com.github.gcmonitor.integration.jmx.data.GcMonitorData;
import com.github.gcmonitor.integration.jmx.data.type.GcMonitorDataType;

public class GcMonitorImpl implements GcMonitorMXBean {

    private final GcMonitor gcMonitor;
    private final GcMonitorDataType type;

    public GcMonitorImpl(GcMonitor gcMonitor) {
        this.gcMonitor = gcMonitor;
        this.type = GcMonitorDataType.buildCompositeType(gcMonitor.getCollectorNames(), gcMonitor.getConfiguration());
    }

    @Override
    public GcMonitorData getGcMonitorData() {
        GcMonitorData[] dataRef = new GcMonitorData[1];
        gcMonitor.getStatistics(statistics -> dataRef[0] = new GcMonitorData(type, statistics));
        return dataRef[0];
    }

}
