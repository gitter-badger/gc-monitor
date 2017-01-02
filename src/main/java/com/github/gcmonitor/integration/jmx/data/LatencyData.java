package com.github.gcmonitor.integration.jmx.data;

import com.codahale.metrics.Snapshot;
import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.integration.jmx.data.type.GcLatencyHistogramDataType;
import com.github.gcmonitor.stat.CollectorStatisticsWindow;

import java.util.HashMap;
import java.util.Map;

public class LatencyData extends KeyValueData {

    public LatencyData(GcLatencyHistogramDataType type, CollectorStatisticsWindow window, GcMonitorConfiguration configuration) {
        super(createLatencyValues(type, window, configuration), type);
    }

    private static Map<String, Object> createLatencyValues(GcLatencyHistogramDataType type, CollectorStatisticsWindow window, GcMonitorConfiguration configuration) {
        int decimalPoints = configuration.getDecimalPoints();
        Map<String, Object> values = new HashMap<>();
        Snapshot snapshot = window.getHistogram().getSnapshot();
        type.getExtractors().forEach((key, valueExtractor) -> values.put(key, valueExtractor.apply(snapshot, decimalPoints)));
        return values;
    }

}
