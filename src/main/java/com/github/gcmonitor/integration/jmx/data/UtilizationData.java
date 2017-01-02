package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.integration.jmx.data.type.UtilizationDataType;
import com.github.gcmonitor.stat.CollectorStatisticsWindow;
import com.github.gcmonitor.util.Formatter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class UtilizationData extends KeyValueData {

    public UtilizationData(UtilizationDataType type, CollectorStatisticsWindow window, GcMonitorConfiguration configuration) {
        super(createUtilizationValues(window, configuration), type);
    }

    private static Map<String, Object> createUtilizationValues(CollectorStatisticsWindow window, GcMonitorConfiguration configuration) {
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
