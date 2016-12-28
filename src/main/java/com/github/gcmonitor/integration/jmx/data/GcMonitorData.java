package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.integration.jmx.data.type.GcCollectorDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcMonitorDataType;
import com.github.gcmonitor.stat.CollectorStatistics;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GcMonitorData implements CompositeData {

    private final Map<String, GcCollectorData> data;
    private final GcMonitorDataType type;

    public GcMonitorData(GcMonitorDataType type, Map<String, CollectorStatistics> statistics) {
        this.type = type;
        this.data = new HashMap<>();
        statistics.forEach((collectorName, collectorStatistics) -> {
            GcCollectorDataType collectorDataType = type.getCollectorType(collectorName);
            GcCollectorData collectorData = new GcCollectorData(collectorDataType, collectorStatistics);
            data.put(collectorName, collectorData);
        });
    }

    @Override
    public CompositeType getCompositeType() {
        return type;
    }

    @Override
    public Object get(String key) {
        return data.get(key);
    }

    @Override
    public Object[] getAll(String[] keys) {
        Object[] values = new Object[keys.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = data.get(keys[i]);
        }
        return values;
    }

    @Override
    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Collection<?> values() {
        return data.values();
    }

}
