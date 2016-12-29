package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.integration.jmx.data.type.GcCollectorWindowDataType;
import com.github.gcmonitor.stat.CollectorStatisticsWindow;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.util.Arrays;
import java.util.Collection;


public class GcCollectorWindowData implements CompositeData {

    private final LatencyData latencyData;
    private final UtilizationData utilizationData;
    private final GcCollectorWindowDataType type;

    GcCollectorWindowData(GcCollectorWindowDataType windowType, CollectorStatisticsWindow window) {
        this.latencyData = new LatencyData(windowType.getHistogramType(), window);
        this.utilizationData = new UtilizationData(windowType.getUtilizationType(), window);
        this.type = windowType;
    }

    @Override
    public CompositeType getCompositeType() {
        return type;
    }

    @Override
    public Object get(String key) {
        if (GcCollectorWindowDataType.HISTOGRAM_ITEM.equals(key)) {
            return latencyData;
        } else if (GcCollectorWindowDataType.UTILIZATION_ITEM.equals(key)) {
            return utilizationData;
        } else {
            throw new IllegalArgumentException(key + " - is unknown key");
        }
    }

    @Override
    public Object[] getAll(String[] keys) {
        return new Object[] {latencyData, utilizationData};
    }

    @Override
    public boolean containsKey(String key) {
        return GcCollectorWindowDataType.HISTOGRAM_ITEM.equals(key)
                || GcCollectorWindowDataType.UTILIZATION_ITEM.equals(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return latencyData == value || utilizationData == value;
    }

    @Override
    public Collection<?> values() {
        return Arrays.asList(latencyData, utilizationData);
    }

}
