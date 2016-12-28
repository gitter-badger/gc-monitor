package com.github.gcmonitor.integration.jmx.data.type;

import com.github.gcmonitor.stat.CollectorStatistics;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

public class GcCollectorWindowDataType extends CompositeType {

    public static final String TYPE_NAME = GcMonitorDataType.TYPE_NAME + ".collector";
    private static final String DESCRIPTION = "Shows aggregated information about particular garbage collector.";


    public static GcCollectorWindowDataType buildCompositeType(String collectorName, CollectorStatistics collectorStatistics) {

        // TODO
        return null;
    }

    public GcCollectorWindowDataType(String typeName, String description, String[] itemNames, String[] itemDescriptions, OpenType<?>[] itemTypes) throws OpenDataException {
        super(typeName, description, itemNames, itemDescriptions, itemTypes);
    }

}
