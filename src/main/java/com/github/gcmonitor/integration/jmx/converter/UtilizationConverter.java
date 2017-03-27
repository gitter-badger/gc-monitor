package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.CollectorWindowSnapshot;
import com.github.gcmonitor.stat.Formatter;
import com.github.gcmonitor.stat.GcMonitorSnapshot;

import javax.management.openmbean.*;
import java.math.BigDecimal;


public class UtilizationConverter implements Converter {

    public static final String TYPE_NAME = WindowConverter.TYPE_NAME + ".utilization";
    public static final String TYPE_DESCRIPTION = "Shows information about collector utilization.";

    public static final String DURATION_FIELD = "pauseDurationMillis";
    public static final String PERCENTAGE_FIELD = "pausePercentage";

    private static final String[] ITEM_NAMES = new String[] {
            DURATION_FIELD, PERCENTAGE_FIELD
    };

    private final GcMonitorConfiguration configuration;
    private final String collectorName;
    private final String windowName;
    private CompositeType type;

    public UtilizationConverter(GcMonitorConfiguration configuration, String collectorName, String windowName) throws OpenDataException {
        this.configuration = configuration;
        this.collectorName = collectorName;
        this.windowName = windowName;

        String[] itemDescriptions = new String[] {
                "Time in milliseconds which JVM spent in STW GC pauses instead of doing real work",
                "Percentage of time which JVM spent in STW GC pauses instead of doing real work",
        };
        OpenType<?>[] itemTypes = new OpenType<?>[] {
                SimpleType.LONG,
                SimpleType.BIGDECIMAL
        };
        this.type = new CompositeType(
                TYPE_NAME,
                TYPE_DESCRIPTION,
                ITEM_NAMES,
                itemDescriptions,
                itemTypes
        );
    }

    @Override
    public CompositeData map(GcMonitorSnapshot snapshot) {
        CollectorWindowSnapshot windowSnapshot = snapshot.getCollectorWindowSnapshot(collectorName, windowName);

        long gcDuration = windowSnapshot.getMillisSpentInGc();
        double percentage = windowSnapshot.getPercentageSpentInGc();
        BigDecimal formattedPercentage = Formatter.roundToDigits(percentage, configuration.getDecimalPoints());
        Object[] itemValues = {gcDuration, formattedPercentage};
        try {
            return new CompositeDataSupport(type, ITEM_NAMES, itemValues);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompositeType getType() {
        return type;
    }

}
