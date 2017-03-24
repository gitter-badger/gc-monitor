package com.github.gcmonitor.integration.jmx;

import com.codahale.metrics.Snapshot;
import com.github.gcmonitor.integration.jmx.data.GcCollectorData;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorWindowDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcLatencyHistogramDataType;
import com.github.gcmonitor.stat.*;

import javax.management.openmbean.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class JmxConverter {

    public static final String GC_MONITOR_TYPE_DESCRIPTION = "Shows aggregated information about garbage collectors";
    public static final String GC_MONITOR_TYPE_NAME = "com.github.gcmonitor.stat";

    public static final String GC_MONITOR_COLLECTOR_TYPE_DESCRIPTION = "Shows aggregated information about particular garbage collector";
    public static final String GC_MONITOR_COLLECTOR_TYPE_NAME = GC_MONITOR_TYPE_NAME + ".collector";

    public static final String GC_MONITOR_COLLECTOR_WINDOW_TYPE_DESCRIPTION = "Shows aggregated information about particular garbage collector window";
    public static final String GC_MONITOR_COLLECTOR_WINDOW_TYPE_NAME = GC_MONITOR_COLLECTOR_TYPE_NAME + ".window";

    public static final String DURATION_FIELD = "stwDurationMillis";
    public static final String STW_PERCENTAGE_FIELD = "stwPercentage";

    private final CompositeType monitorType;
    private final Map<String, CompositeType> collectorTypes;
    private final Map<String, Map<String, CompositeType>> collectorWindowTypes;

    public JmxConverter(GcMonitorConfiguration configuration) {
        try {
            this.collectorWindowTypes = buildCollectorWindowTypes(configuration);
            this.collectorTypes = buildCollectorTypes(configuration, collectorWindowTypes);
            this.monitorType = buildGcMonitorCompositeType(configuration, collectorTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
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

    private CompositeType buildGcMonitorCompositeType(GcMonitorConfiguration configuration, Map<String, CompositeType> collectorTypes) {
        Set<String> collectorNames = configuration.getCollectorNames();

        final Map<String, GcCollectorDataType> collectorTypes;
        String[] itemNames = new String[collectorNames.size()];
        String[] itemDescriptions = new String[collectorNames.size()];
        OpenType<?>[] itemTypes = new OpenType<?>[collectorNames.size()];
        Map<String, GcCollectorDataType> collectorTypes = new HashMap<>();
        int i = 0;
        GcCollectorDataType collectorType = GcCollectorDataType.buildCompositeType(configuration);
        for (String collectorName : collectorNames) {
            collectorTypes.put(collectorName, collectorType);
            itemNames[i] = collectorName;
            itemDescriptions[i] = "Shows aggregated information about garbage collector [" + collectorName + "]";
            itemTypes[i] = collectorType;
            i++;
        }

        try {
            return new CompositeType(GC_MONITOR_TYPE_NAME, GC_MONITOR_TYPE_DESCRIPTION, itemNames, itemDescriptions, itemTypes);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    public CompositeData map(GcMonitorSnapshot snapshot) {
        return null;
    }

    private static Map<String, Object> createMonitorValues(GcMonitorDataType type, Map<String, CollectorStatistics> statistics, GcMonitorConfiguration configuration) {
        HashMap<String, Object> data = new HashMap<>();
        statistics.forEach((collectorName, collectorStatistics) -> {
            GcCollectorDataType collectorDataType = type.getCollectorType(collectorName);
            GcCollectorData collectorData = new GcCollectorData(collectorDataType, collectorStatistics, configuration);
            data.put(collectorName, collectorData);
        });
        return data;
    }

    private static Map<String, Object> createUtilizationValues(CollectorWindow window, GcMonitorConfiguration configuration) {
        Map<String, Object> values = new HashMap<>();
        Long gcDuration = window.getCounter().getSum();
        values.put(UtilizationDataType.DURATION_FIELD, gcDuration);

        double percentage = (double) gcDuration /
                (double) TimeUnit.SECONDS.toMillis(window.getWindowDurationSeconds())
                * 100;
        BigDecimal formattedPercentage = Formatter.roundToDigits(percentage, configuration.getDecimalPoints());
        values.put(UtilizationDataType.PERCENTAGE_FIELD, formattedPercentage);
        return values;
    }

    private static Map<String, Object> createLatencyValues(GcLatencyHistogramDataType type, CollectorWindow window, GcMonitorConfiguration configuration) {
        int decimalPoints = configuration.getDecimalPoints();
        Map<String, Object> values = new HashMap<>();
        Snapshot snapshot = window.getHistogram().getSnapshot();
        type.getExtractors().forEach((key, valueExtractor) -> values.put(key, valueExtractor.apply(snapshot, decimalPoints)));
        return values;
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

    private static class GcLatencyHistogramDataType {

    }

    private static class GcLatencyHistogramDataType {

    }

    private static class GcLatencyHistogramDataType {

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

        public static GcLatencyHistogramDataType buildCompositeType(GcMonitorConfiguration configuration) {
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
                return new GcLatencyHistogramDataType(itemNames, itemDescriptions, itemTypes, extractors);
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

}
