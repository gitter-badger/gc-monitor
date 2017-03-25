package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.CollectorWindowSnapshot;

import javax.management.openmbean.*;
import java.util.HashMap;
import java.util.Map;

public class WindowConverter implements Converter<CollectorWindowSnapshot> {

    public static final String TYPE_DESCRIPTION = "Shows aggregated information about particular garbage collector window";
    public static final String TYPE_NAME = CollectorConverter.TYPE_NAME + ".window";

    private final CompositeType type;

    public WindowConverter(GcMonitorConfiguration configuration, String collectorName, String windowName) {
        // todo
        this.type = type;
    }

    @Override
    public CompositeData map(CollectorWindowSnapshot source) {
        return null;
    }

    @Override
    public CompositeType getType() {
        return type;
    }

    private Map<String, Map<String, CompositeType>> buildCollectorWindowTypes(GcMonitorConfiguration configuration) throws OpenDataException {
        Map<String, Map<String, CompositeType>> result = new HashMap<>();
        for (String collectorName : configuration.getCollectorNames()) {
            Map<String, CompositeType> collectorMap = new HashMap<>();
            result.put(collectorName, collectorMap);
            for (String windowName : configuration.getWindowNames()) {
                String[] names = new String[] {
                        DURATION_FIELD,
                        STW_PERCENTAGE_FIELD
                };
                String[] descriptions = new String[] {
                        "Time in milliseconds which JVM spent in STW GC pauses instead of doing real work",
                        "Percentage of time which JVM spent in STW GC pauses instead of doing real work",
                };
                OpenType<?>[] types = new OpenType<?>[] {
                        SimpleType.LONG,
                        SimpleType.BIGDECIMAL
                };
                CompositeType windowType = new CompositeType(
                        GC_MONITOR_COLLECTOR_WINDOW_TYPE_NAME,
                        GC_MONITOR_COLLECTOR_WINDOW_TYPE_DESCRIPTION,
                        names,
                        descriptions,
                        types
                );
                collectorMap.put(windowName, windowType);
            }
        }
        return result;
    }

}
