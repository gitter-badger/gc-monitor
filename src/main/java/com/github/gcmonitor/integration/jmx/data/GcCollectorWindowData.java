package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.integration.jmx.data.type.GcCollectorWindowDataType;
import com.github.gcmonitor.stat.CollectorStatisticsWindow;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.util.Collection;

public class GcCollectorWindowData implements CompositeData {

    public GcCollectorWindowData(GcCollectorWindowDataType windowType, CollectorStatisticsWindow window) {
        // TODO
    }

    @Override
    public CompositeType getCompositeType() {
        return null;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public Object[] getAll(String[] keys) {
        return new Object[0];
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Collection<?> values() {
        return null;
    }

}
