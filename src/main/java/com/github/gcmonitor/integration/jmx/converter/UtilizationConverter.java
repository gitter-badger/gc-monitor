package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.CollectorWindow;
import com.github.gcmonitor.stat.CollectorWindowSnapshot;
import com.github.gcmonitor.stat.Formatter;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UtilizationConverter implements Converter<CollectorWindowSnapshot> {

    public static final String DURATION_FIELD = "stwDurationMillis";
    public static final String STW_PERCENTAGE_FIELD = "stwPercentage";



    @Override
    public CompositeData map(CollectorWindowSnapshot source) {
        return null;
    }

    @Override
    public CompositeType getType() {
        return null;
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

}
