package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.CollectorWindow;
import com.github.gcmonitor.stat.GcMonitorSnapshot;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import java.util.*;

public class CollectorConverter implements Converter {

    static final String TYPE_DESCRIPTION = "Shows aggregated information about particular garbage collector";
    static final String TYPE_NAME = SnapshotConverter.TYPE_NAME + ".collector";

    private final CompositeType type;
    private final SortedMap<String, WindowConverter> windowConverters = new TreeMap<>();

    CollectorConverter(GcMonitorConfiguration configuration, String collectorName) throws OpenDataException {
        Set<String> windowNames = configuration.getWindowNames();
        String[] itemNames = new String[windowNames.size()];
        String[] itemDescriptions = new String[windowNames.size()];
        OpenType<?>[] itemTypes = new OpenType<?>[windowNames.size()];

        int i = 0;
        for (String windowName : windowNames) {
            WindowConverter windowConverter = new WindowConverter(configuration, collectorName, windowName);
            itemNames[i] = windowName;
            itemDescriptions[i] = windowName;
            itemTypes[i] = windowConverter.getType();
            i++;
        }

        this.type = new CompositeType(TYPE_NAME, TYPE_DESCRIPTION, itemNames, itemDescriptions, itemTypes);
    }

    @Override
    public CompositeData map(GcMonitorSnapshot snapshot) {
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

    @Override
    public CompositeType getType() {
        return type;
    }

}
