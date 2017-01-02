package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcMonitorDataType;
import com.github.gcmonitor.stat.CollectorStatistics;

import java.util.HashMap;
import java.util.Map;

public class GcMonitorData extends KeyValueData {

    public GcMonitorData(GcMonitorDataType type, Map<String, CollectorStatistics> statistics, GcMonitorConfiguration configuration) {
        super(createMonitorValues(type, statistics, configuration), type);
    }

    private static Map<String, Object> createMonitorValues(GcMonitorDataType type, Map<String, CollectorStatistics> statistics, GcMonitorConfiguration configuration) {
        HashMap<String, Object> data = new HashMap<>();
        statistics.forEach((collectorName, collectorStatistics) -> {
            GcCollectorDataType collectorDataType = type.getCollectorType(collectorName);
            GcCollectorData collectorData = new GcCollectorData(collectorDataType, collectorStatistics, configuration);
            data.put(collectorName, collectorData);
        });
        return data;
    }

}
