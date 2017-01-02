package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorWindowDataType;
import com.github.gcmonitor.stat.CollectorStatistics;
import com.github.gcmonitor.stat.CollectorStatisticsWindow;

import java.util.HashMap;
import java.util.Map;

public class GcCollectorData extends KeyValueData {

    public GcCollectorData(GcCollectorDataType type, CollectorStatistics collectorStatistics, GcMonitorConfiguration configuration) {
        super(createCollectorValues(type, collectorStatistics, configuration), type);
    }

    private static Map<String, Object> createCollectorValues(GcCollectorDataType type, CollectorStatistics collectorStatistics, GcMonitorConfiguration configuration) {
        HashMap<String, Object> values = new HashMap<>();
        GcCollectorWindowDataType windowType = type.getWindowType();
        for (CollectorStatisticsWindow window : collectorStatistics.getWindows()) {
            long duration = window.getWindowDurationSeconds();
            String windowName = type.getWindowName(duration);
            GcCollectorWindowData windowData = new GcCollectorWindowData(windowType, window, configuration);
            values.put(windowName, windowData);
        }
        return values;
    }

}
