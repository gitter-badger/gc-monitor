package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.GcMonitorSnapshot;
import javax.management.openmbean.*;

public class WindowConverter implements Converter {

    public static final String TYPE_DESCRIPTION = "Shows aggregated information about particular garbage collector window";
    public static final String TYPE_NAME = CollectorConverter.TYPE_NAME + ".window";

    private static final String UTILIZATION_FIELD = "utilization";
    private static final String PAUSE_HISTOGRAM_FIELD = "pauseHistogram";
    private static final String[] itemNames = new String[] {
            UTILIZATION_FIELD,
            PAUSE_HISTOGRAM_FIELD
    };

    private final CompositeType type;
    private final UtilizationConverter utilizationConverter;
    private final LatencyHistogramConverter latencyHistogramConverter;

    public WindowConverter(GcMonitorConfiguration configuration, String collectorName, String windowName) throws OpenDataException {
        this.utilizationConverter = new UtilizationConverter(configuration, collectorName, windowName);
        this.latencyHistogramConverter = new LatencyHistogramConverter(configuration, collectorName, windowName);

        String[] itemDescriptions = new String[] {
                "GC latency histogram",
                "Collector utilization"
        };
        OpenType<?>[] itemTypes = new OpenType<?>[] {
                utilizationConverter.getType(),
                latencyHistogramConverter.getType()
        };
        this.type = new CompositeType(TYPE_NAME, TYPE_DESCRIPTION, itemNames, itemDescriptions, itemTypes);
    }

    @Override
    public CompositeData map(GcMonitorSnapshot snapshot) {
        Object[] itemValues = {
                utilizationConverter.map(snapshot),
                latencyHistogramConverter.map(snapshot)
        };
        try {
            return new CompositeDataSupport(type, itemNames, itemValues);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompositeType getType() {
        return type;
    }

}
