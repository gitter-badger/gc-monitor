package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.*;

import javax.management.openmbean.*;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class SnapshotConverter implements Converter {

    static final String TYPE_DESCRIPTION = "Shows aggregated information about garbage collectors";
    static final String TYPE_NAME = "com.github.gcmonitor.snapshot";

    private final CompositeType type;
    private final SortedMap<String, CollectorConverter> collectorConverters = new TreeMap<>();

    public SnapshotConverter(GcMonitorConfiguration configuration) throws OpenDataException {
        Set<String> collectorNames = configuration.getCollectorNames();
        String[] itemNames = new String[collectorNames.size()];
        String[] itemDescriptions = new String[collectorNames.size()];
        OpenType<?>[] itemTypes = new OpenType<?>[collectorNames.size()];

        int i = 0;
        for (String collectorName : collectorNames) {
            CollectorConverter collectorConverter = new CollectorConverter(configuration, collectorName);
            collectorConverters.put(collectorName, collectorConverter);
            itemNames[i] = collectorName;
            itemDescriptions[i] = "Shows aggregated information about garbage collector [" + collectorName + "]";
            itemTypes[i] = collectorConverter.getType();
            i++;
        }

        this.type = new CompositeType(TYPE_NAME, TYPE_DESCRIPTION, itemNames, itemDescriptions, itemTypes);
    }

    public CompositeData map(GcMonitorSnapshot snapshot) {
        HashMap<String, Object> data = new HashMap<>();
        collectorConverters.forEach((name, converter) -> {
            data.put(name, converter.map(snapshot));
        });
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
