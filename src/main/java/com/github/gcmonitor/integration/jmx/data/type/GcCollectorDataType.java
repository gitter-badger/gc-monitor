package com.github.gcmonitor.integration.jmx.data.type;

import com.github.gcmonitor.integration.jmx.data.GcCollectorWindowData;
import com.github.gcmonitor.stat.CollectorStatistics;
import com.github.gcmonitor.stat.CollectorStatisticsWindow;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import java.util.Map;

public class GcCollectorDataType extends CompositeType {

    public static final String TYPE_NAME = GcMonitorDataType.TYPE_NAME + ".collector";
    private static final String DESCRIPTION = "Shows aggregated information about particular garbage collector.";

    private final Map<Long, GcCollectorWindowDataType> windowTypes;

    public static GcCollectorDataType buildCompositeType(String collectorName, CollectorStatistics collectorStatistics) {
        for (CollectorStatisticsWindow window : collectorStatistics.getWindows()) {

        }

        // TODO
        return null;
    }

    public GcCollectorWindowDataType getWindowType(long windowDurtion) {
        return windowTypes
    }

    public GcCollectorDataType(String typeName, String description, String[] itemNames, String[] itemDescriptions, OpenType<?>[] itemTypes, Map<Long, GcCollectorWindowDataType> windowTypes) throws OpenDataException {
        super(typeName, description, itemNames, itemDescriptions, itemTypes);
        this.windowTypes = windowTypes;
    }

}
