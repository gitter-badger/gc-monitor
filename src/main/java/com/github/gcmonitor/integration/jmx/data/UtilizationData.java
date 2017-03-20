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

package com.github.gcmonitor.integration.jmx.data;

import com.github.gcmonitor.stat.CollectorWindow;
import com.github.gcmonitor.stat.GcMonitorConfiguration;
import com.github.gcmonitor.integration.jmx.data.type.UtilizationDataType;
import com.github.gcmonitor.stat.Formatter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class UtilizationData extends KeyValueData {

    public UtilizationData(UtilizationDataType type, CollectorWindow window, GcMonitorConfiguration configuration) {
        super(createUtilizationValues(window, configuration), type);
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
