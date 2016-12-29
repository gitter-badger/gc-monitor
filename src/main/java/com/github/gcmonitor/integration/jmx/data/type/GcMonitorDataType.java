package com.github.gcmonitor.integration.jmx.data.type;

import com.github.gcmonitor.GcMonitorConfiguration;

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

    public static GcMonitorDataType buildCompositeType(Set<String> collectorNames, GcMonitorConfiguration configuration) {
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
            return new GcMonitorDataType(itemNames, itemDescriptions, itemTypes, collectorTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    public GcMonitorDataType(String[] itemNames, String[] itemDescriptions, OpenType<?>[] itemTypes, Map<String, GcCollectorDataType> collectorTypes) throws OpenDataException {
        super(TYPE_NAME, DESCRIPTION, itemNames, itemDescriptions, itemTypes);
        this.collectorTypes = collectorTypes;
    }

    public GcCollectorDataType getCollectorType(String collectorName) {
        return collectorTypes.get(collectorName);
    }

}
