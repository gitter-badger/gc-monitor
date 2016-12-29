package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.integration.jmx.data.type.GcCollectorDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorWindowDataType;
import com.github.gcmonitor.stat.CollectorStatistics;
import com.github.gcmonitor.stat.CollectorStatisticsWindow;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GcCollectorData implements CompositeData {

    private final GcCollectorDataType type;
    private final Map<String, GcCollectorWindowData> data;

    public GcCollectorData(GcCollectorDataType type, CollectorStatistics collectorStatistics) {
        this.type = type;
        this.data = new HashMap<>();
        GcCollectorWindowDataType windowType = type.getWindowType();
        for (CollectorStatisticsWindow window : collectorStatistics.getWindows()) {
            long duration = window.getWindowDurationSeconds();
            String windowName = type.getWindowName(duration);
            GcCollectorWindowData windowData = new GcCollectorWindowData(windowType, window);
            data.put(windowName, windowData);
        }
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
        for (int i = 0; i < keys.length; i++) {
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
