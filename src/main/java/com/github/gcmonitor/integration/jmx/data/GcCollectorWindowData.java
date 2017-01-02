package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorWindowDataType;
import com.github.gcmonitor.stat.CollectorStatisticsWindow;

import java.util.HashMap;
import java.util.Map;


public class GcCollectorWindowData extends KeyValueData {

    public GcCollectorWindowData(GcCollectorWindowDataType type, CollectorStatisticsWindow window, GcMonitorConfiguration configuration) {
        super(createWindowValues(type, window, configuration), type);
    }

    private static Map<String, Object> createWindowValues(GcCollectorWindowDataType type, CollectorStatisticsWindow window, GcMonitorConfiguration configuration) {
        Map<String, Object> values = new HashMap<>();
        values.put(GcCollectorWindowDataType.HISTOGRAM_ITEM, new LatencyData(type.getHistogramType(), window, configuration));
        values.put(GcCollectorWindowDataType.UTILIZATION_ITEM, new UtilizationData(type.getUtilizationType(), window, configuration));
        return values;
    }

}
