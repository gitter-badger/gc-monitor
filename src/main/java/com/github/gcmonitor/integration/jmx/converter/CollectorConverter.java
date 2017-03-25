package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.CollectorStatistics;
import com.github.gcmonitor.stat.CollectorWindow;
import com.github.gcmonitor.stat.CollectorWindowSnapshot;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import java.util.HashMap;
import java.util.Map;

public class CollectorConverter implements Converter<Map<String, CollectorWindowSnapshot>> {

    public static final String TYPE_DESCRIPTION = "Shows aggregated information about particular garbage collector";
    public static final String TYPE_NAME = MonitorSnapshotConverter.TYPE_NAME + ".collector";

    private final CompositeType type;

    public CollectorConverter(GcMonitorConfiguration configuration, String collectorName) {
        this.type = type;
    }

    @Override
    public CompositeData map(Map<String, CollectorWindowSnapshot> source) {
        // todo
        return null;
    }

    @Override
    public CompositeType getType() {
        return type;
    }

    private static Map<String, Object> createCollectorValues(GcCollectorDataType type, CollectorStatistics collectorStatistics, GcMonitorConfiguration configuration) {
        HashMap<String, Object> values = new HashMap<>();
        GcCollectorWindowDataType windowType = type.getWindowType();
        for (CollectorWindow window : collectorStatistics.getWindows()) {
            long duration = window.getWindowDurationSeconds();
            String windowName = type.getWindowName(duration);
            GcCollectorWindowData windowData = new GcCollectorWindowData(windowType, window, configuration);
            values.put(windowName, windowData);
        }
        return values;
    }

    private Map<String, CompositeType> buildCollectorTypes(GcMonitorConfiguration configuration, Map<String, Map<String, CompositeType>> collectorWindowTypes) {
        Map<String, CompositeType> result = new HashMap<>();
        for (String collectorName : configuration.getCollectorNames()) {
            long[] timeWindows = configuration.getWindowSpecifications();

            Map<Long, String> namesByDuration = new HashMap<>();
            String[] itemNames = new String[timeWindows.length];
            String[] itemDescriptions = new String[timeWindows.length];
            OpenType<?>[] itemTypes = new OpenType<?>[timeWindows.length];

            GcCollectorWindowDataType windowType = GcCollectorWindowDataType.buildCompositeType(configuration);
            for (int i = 0; i < timeWindows.length; i++) {
                long durationSeconds = timeWindows[i];
                String windowName = durationSeconds + " second window";
                namesByDuration.put(durationSeconds, windowName);
                itemNames[i] = windowName;
                itemDescriptions[i] = windowName;
                itemTypes[i] = windowType;
            }
            // TODO
            CompositeType collectorType = null;
            result.put(collectorName, collectorType);
        }
        return result;
    }

}
