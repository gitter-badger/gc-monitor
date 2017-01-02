package com.github.gcmonitor.integration.jmx;

import com.github.gcmonitor.GcMonitor;
import com.github.gcmonitor.integration.jmx.data.GcMonitorData;
import com.github.gcmonitor.integration.jmx.data.type.GcMonitorDataType;


public class GcMonitorStatistics implements GcMonitorStatisticsMXBean {

    private final GcMonitor gcMonitor;
    private final GcMonitorDataType type;

    public GcMonitorStatistics(GcMonitor gcMonitor) {
        this.gcMonitor = gcMonitor;
        this.type = GcMonitorDataType.buildCompositeType(gcMonitor.getCollectorNames(), gcMonitor.getConfiguration());
    }

    @Override
    public GcMonitorData getGcMonitorData() {
        try {
            GcMonitorData[] dataRef = new GcMonitorData[1];
            gcMonitor.getStatistics(statistics -> dataRef[0] = new GcMonitorData(type, statistics, gcMonitor.getConfiguration()));
            GcMonitorData result = dataRef[0];
            return result;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

}
