package com.github.gcmonitor.integration.jmx.data.type;

import com.github.gcmonitor.GcMonitorConfiguration;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GcLatencyHistogramDataType extends CompositeType {

    public static String MIN_NAME = "min";
    public static String MAX_NAME = "max";
    public static String MEAN_NAME = "mean";
    public static String MEDIAN_NAME = "median";
    public static String STD_DEVIATION_NAME = "stdDeviation";

    public static final String TYPE_NAME = GcCollectorWindowDataType.TYPE_NAME + ".histogram";
    public static final String DESCRIPTION = "Shows latencies of GC pauses for collector.";

    private static String[] predefinedItemNames = new String[] {
            MIN_NAME,
            MAX_NAME,
            MEAN_NAME,
            MEDIAN_NAME,
            STD_DEVIATION_NAME
    };
    private static String[] predefinedItemDescriptions = new String[] {
            "Minimum milliseconds",
            "Maximum milliseconds",
            "Average milliseconds",
            "Median milliseconds",
            "Standard deviation milliseconds",
    };
    private static OpenType<?>[] predefinedItemTypes = new OpenType<?>[] {
            SimpleType.LONG,
            SimpleType.LONG,
            SimpleType.BIGDECIMAL,
            SimpleType.BIGDECIMAL,
            SimpleType.BIGDECIMAL
    };

    private final Map<Double, String> percentileToName;

    public String getPercentileName(Double percentile) {
        return percentileToName.get(percentile);
    }

    public Set<Double> getPercentiles() {
        return percentileToName.keySet();
    }

    public static GcLatencyHistogramDataType buildCompositeType(GcMonitorConfiguration configuration) {
        double[] percentiles = configuration.getPercentiles();
        int percentileCount = percentiles.length;
        String[] itemNames = Arrays.copyOf(predefinedItemNames, predefinedItemNames.length + percentileCount);
        String[] itemDescriptions = Arrays.copyOf(predefinedItemDescriptions, predefinedItemNames.length + percentileCount);;
        OpenType<?>[] itemTypes = Arrays.copyOf(predefinedItemTypes, percentileCount);
        Map<Double, String> percentileToName = new HashMap<>();

        for (int i = 0; i < percentileCount; i++) {
            double percentile = percentiles[i];
            String printablePercentileName = printablePercentile(percentile);
            percentileToName.put(percentile, printablePercentileName);
            itemNames[predefinedItemNames.length + i] = printablePercentileName;
            itemDescriptions[predefinedItemDescriptions.length + i] = printablePercentileName;
            itemTypes[predefinedItemTypes.length + i] = SimpleType.BIGDECIMAL;
        }

        try {
            return new GcLatencyHistogramDataType(itemNames, itemDescriptions, itemTypes, percentileToName);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String printablePercentile(double percentile) {
        while (Math.floor(percentile) != percentile || Math.ceil(percentile) != percentile) {
            percentile = percentile * 10;
        }
        return "" + percentile + "thPercentile";
    }

    public GcLatencyHistogramDataType(String[] itemNames, String[] itemDescriptions, OpenType<?>[] itemTypes, Map<Double, String> percentileToName) throws OpenDataException {
        super(TYPE_NAME, DESCRIPTION, itemNames, itemDescriptions, itemTypes);
        this.percentileToName = percentileToName;
    }

}
