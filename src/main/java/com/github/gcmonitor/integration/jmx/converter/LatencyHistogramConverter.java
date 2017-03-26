package com.github.gcmonitor.integration.jmx.converter;

import com.codahale.metrics.Snapshot;
import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.CollectorWindow;
import com.github.gcmonitor.stat.Formatter;
import com.github.gcmonitor.stat.GcMonitorSnapshot;

import javax.management.openmbean.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class LatencyHistogramConverter implements Converter {

    public static String MIN_NAME = "min";
    public static String MAX_NAME = "max";
    public static String MEAN_NAME = "mean";
    public static String MEDIAN_NAME = "median";
    public static String STD_DEVIATION_NAME = "stdDeviation";

    public static final String TYPE_NAME = WindowConverter.TYPE_NAME + ".histogram";
    public static final String TYPE_DESCRIPTION = "Shows latencies of GC pauses for collector.";

    public LatencyHistogramConverter(GcMonitorConfiguration configuration, String collectorName, String windowName) {
        this.extractors = extractors;
    }

    @Override
    public CompositeData map(GcMonitorSnapshot snapshot) {
        return null;
    }

    @Override
    public CompositeType getType() {
        return null;
    }

    private static Map<String, Object> createLatencyValues(SnapshotConverter.GcLatencyHistogramDataType type, CollectorWindow window, GcMonitorConfiguration configuration) {
        int decimalPoints = configuration.getDecimalPoints();
        Map<String, Object> values = new HashMap<>();
        Snapshot snapshot = window.getHistogram().getSnapshot();
        type.getExtractors().forEach((key, valueExtractor) -> values.put(key, valueExtractor.apply(snapshot, decimalPoints)));
        return values;
    }

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

    private static Map<String, BiFunction<Snapshot, Integer, Object>> predefinedExtractors = new HashMap<>(); static {
        predefinedExtractors.put(MIN_NAME, (snapshot, decimalPoints) -> snapshot.getMin());
        predefinedExtractors.put(MAX_NAME, (snapshot, decimalPoints) -> snapshot.getMax());
        predefinedExtractors.put(MEAN_NAME, (snapshot, decimalPoints) -> Formatter.roundToDigits(snapshot.getMean(), decimalPoints));
        predefinedExtractors.put(MEDIAN_NAME, (snapshot, decimalPoints) -> Formatter.roundToDigits(snapshot.getMedian(), decimalPoints));
        predefinedExtractors.put(STD_DEVIATION_NAME, (snapshot, decimalPoints) -> Formatter.roundToDigits(snapshot.getStdDev(), decimalPoints));
    }

    private final Map<String, BiFunction<Snapshot, Integer, Object>> extractors;

    public Map<String, BiFunction<Snapshot, Integer, Object>> getExtractors() {
        return extractors;
    }

    public static SnapshotConverter.GcLatencyHistogramDataType buildCompositeType(GcMonitorConfiguration configuration) {
        double[] percentiles = configuration.getPercentiles();
        int percentileCount = percentiles.length;
        String[] itemNames = Arrays.copyOf(predefinedItemNames, predefinedItemNames.length + percentileCount);
        String[] itemDescriptions = Arrays.copyOf(predefinedItemDescriptions, predefinedItemNames.length + percentileCount);
        OpenType<?>[] itemTypes = Arrays.copyOf(predefinedItemTypes, predefinedItemTypes.length + percentileCount);

        Map<String, BiFunction<Snapshot, Integer, Object>> extractors = new HashMap<>(predefinedExtractors);
        for (int i = 0; i < percentileCount; i++) {
            double percentile = percentiles[i];
            String printablePercentileName = printablePercentile(percentile);
            extractors.put(printablePercentileName,
                    (snapshot, decimalPoints) -> Formatter.roundToDigits(snapshot.getValue(percentile), decimalPoints));
            itemNames[predefinedItemNames.length + i] = printablePercentileName;
            itemDescriptions[predefinedItemDescriptions.length + i] = printablePercentileName;
            itemTypes[predefinedItemTypes.length + i] = SimpleType.BIGDECIMAL;
        }

        try {
            return new SnapshotConverter.GcLatencyHistogramDataType(itemNames, itemDescriptions, itemTypes, extractors);
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


}
