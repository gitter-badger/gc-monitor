package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.GcMonitorSnapshot;

import javax.management.openmbean.*;
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
            windowConverters.put(windowName, windowConverter);
            itemNames[i] = windowName;
            itemDescriptions[i] = windowName;
            itemTypes[i] = windowConverter.getType();
            i++;
        }

        this.type = new CompositeType(TYPE_NAME, TYPE_DESCRIPTION, itemNames, itemDescriptions, itemTypes);
    }

    @Override
    public CompositeData map(GcMonitorSnapshot snapshot) {
        HashMap<String, Object> data = new HashMap<>();
        windowConverters.forEach((name, converter) -> data.put(name, converter.map(snapshot)));
        try {
            return new CompositeDataSupport(type, data);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompositeType getType() {
        return type;
    }

}
