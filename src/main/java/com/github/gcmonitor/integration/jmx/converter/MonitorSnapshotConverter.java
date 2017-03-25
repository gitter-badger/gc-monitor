package com.github.gcmonitor.integration.jmx.converter;

import com.codahale.metrics.Snapshot;
import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.*;

import javax.management.openmbean.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class MonitorSnapshotConverter implements Converter<GcMonitorSnapshot> {

    public static final String TYPE_DESCRIPTION = "Shows aggregated information about garbage collectors";
    public static final String TYPE_NAME = "com.github.gcmonitor.snapshot";

    private final CompositeType type;

    public MonitorSnapshotConverter(GcMonitorConfiguration configuration) {

    }

    private CompositeType buildGcMonitorCompositeType(GcMonitorConfiguration configuration, Map<String, CompositeType> collectorTypes) {
        Set<String> collectorNames = configuration.getCollectorNames();

        final Map<String, GcCollectorDataType> collectorTypes;
        String[] itemNames = new String[collectorNames.size()];
        String[] itemDescriptions = new String[collectorNames.size()];
        OpenType<?>[] itemTypes = new OpenType<?>[collectorNames.size()];
        Map<String, GcCollectorDataType> collectorTypes = new HashMap<>();
        int i = 0;
        GcCollectorDataType collectorType = GcCollectorDataType.buildCompositeType(configuration);
        for (String collectorName : collectorNames) {
            collectorTypes.put(collectorName, collectorType);
            itemNames[i] = collectorName;
            itemDescriptions[i] = "Shows aggregated information about garbage collector [" + collectorName + "]";
            itemTypes[i] = collectorType;
            i++;
        }

        try {
            return new CompositeType(GC_MONITOR_TYPE_NAME, GC_MONITOR_TYPE_DESCRIPTION, itemNames, itemDescriptions, itemTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    public CompositeData map(GcMonitorSnapshot snapshot) {
        HashMap<String, Object> data = new HashMap<>();
        statistics.forEach((collectorName, collectorStatistics) -> {
            GcCollectorDataType collectorDataType = type.getCollectorType(collectorName);
            GcCollectorData collectorData = new GcCollectorData(collectorDataType, collectorStatistics, configuration);
            data.put(collectorName, collectorData);
        });
        return data;
    }

    @Override
    public CompositeType getType() {
        return type;
    }

}
