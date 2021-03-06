/*
 *  Copyright 2017 Vladimir Bukhtoyarov
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.gcmonitor.integration.jmx.converter;

import com.codahale.metrics.Snapshot;
import io.github.gcmonitor.GcMonitorConfiguration;
import io.github.gcmonitor.stat.Formatter;
import io.github.gcmonitor.stat.GcMonitorSnapshot;

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

    public static final String TYPE_NAME = WindowConverter.TYPE_NAME + ".pauseHistogram";
    public static final String TYPE_DESCRIPTION = "Shows latencies of GC pauses for collector.";

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
    private final GcMonitorConfiguration configuration;
    private final String collectorName;
    private final String windowName;
    private final CompositeType type;

    public LatencyHistogramConverter(GcMonitorConfiguration configuration, String collectorName, String windowName) throws OpenDataException {
        this.configuration = configuration;
        this.collectorName = collectorName;
        this.windowName = windowName;

        double[] percentiles = configuration.getPercentiles();
        int percentileCount = percentiles.length;
        String[] itemNames = Arrays.copyOf(predefinedItemNames, predefinedItemNames.length + percentileCount);
        String[] itemDescriptions = Arrays.copyOf(predefinedItemDescriptions, predefinedItemNames.length + percentileCount);
        OpenType<?>[] itemTypes = Arrays.copyOf(predefinedItemTypes, predefinedItemTypes.length + percentileCount);

        this.extractors = new HashMap<>(predefinedExtractors);
        for (int i = 0; i < percentileCount; i++) {
            double percentile = percentiles[i];
            String printablePercentileName = Formatter.toPrintablePercentileName(percentile);
            extractors.put(printablePercentileName,
                    (snapshot, decimalPoints) -> Formatter.roundToDigits(snapshot.getValue(percentile), decimalPoints));
            itemNames[predefinedItemNames.length + i] = printablePercentileName;
            itemDescriptions[predefinedItemDescriptions.length + i] = printablePercentileName;
            itemTypes[predefinedItemTypes.length + i] = SimpleType.BIGDECIMAL;
        }

        this.type = new CompositeType(TYPE_NAME, TYPE_DESCRIPTION, itemNames, itemDescriptions, itemTypes);
    }

    @Override
    public CompositeData map(GcMonitorSnapshot monitorSnapshot) {
        int decimalPoints = configuration.getDecimalPoints();
        Map<String, Object> values = new HashMap<>();
        Snapshot snapshot = monitorSnapshot.getCollectorWindowSnapshot(collectorName, windowName).getPauseHistogramSnapshot();
        extractors.forEach((key, valueExtractor) -> values.put(key, valueExtractor.apply(snapshot, decimalPoints)));
        try {
            return new CompositeDataSupport(type, values);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompositeType getType() {
        return type;
    }

}
