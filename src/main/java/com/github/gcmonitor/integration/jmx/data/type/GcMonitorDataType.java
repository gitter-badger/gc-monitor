package com.github.gcmonitor.integration.jmx.data.type;

import com.github.gcmonitor.integration.jmx.data.GcCollectorData;
import com.github.gcmonitor.stat.CollectorStatistics;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GcMonitorDataType extends CompositeType {

    public static final String TYPE_NAME = "com.github.gcmonitor.data";
    private static final String DESCRIPTION = "Shows aggregated information about all garbage collectors in JVM.";

    private final Map<String, GcCollectorDataType> collectorTypes;

    public static GcMonitorDataType buildCompositeType(Map<String, CollectorStatistics> statistics) {
        Set<String> collectorNames = statistics.keySet();
        String[] itemNames = new String[collectorNames.size()];
        String[] itemDescriptions = new String[collectorNames.size()];
        OpenType<?>[] itemTypes = new OpenType<?>[collectorNames.size()];
        Map<String, GcCollectorDataType> collectorTypes = new HashMap<>();
        int i = 0;
        for (String collectorName : collectorNames) {
            CollectorStatistics collectorStatistics = statistics.get(collectorName);
            GcCollectorDataType collectorType = GcCollectorDataType.buildCompositeType(collectorName, collectorStatistics);

            collectorTypes.put(collectorName, collectorType);
            itemNames[i] = collectorName;
            itemDescriptions[i] = collectorType.getDescription();
            itemTypes[i] = collectorType;
            i++;
        }

        try {
            return new GcMonitorDataType(TYPE_NAME, DESCRIPTION, itemNames, itemDescriptions, itemTypes, collectorTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    public GcMonitorDataType(String typeName, String description, String[] itemNames, String[] itemDescriptions, OpenType<?>[] itemTypes, Map<String, GcCollectorDataType> collectorTypes) throws OpenDataException {
        super(typeName, description, itemNames, itemDescriptions, itemTypes);
        this.collectorTypes = collectorTypes;
    }

    public GcCollectorDataType getCollectorType(String collectorName) {
        return collectorTypes.get(collectorName);
    }

}
