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

package io.github.gcmonitor.integration.dropwizard;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import io.github.gcmonitor.GcMonitor;
import io.github.gcmonitor.stat.Formatter;
import io.github.gcmonitor.GcMonitorConfiguration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO add javadocs
 */
public class DropwizardAdapter {

    /**
     * TODO add javadocs
     *
     * @param namePrefix
     * @param monitor
     * @return
     */
    public static MetricSet toMetricSet(String namePrefix, GcMonitor monitor) {
        Map<String, Metric> metrics = new HashMap<>();
        GcMonitorConfiguration configuration = monitor.getConfiguration();
        for (String collectorName : configuration.getCollectorNames()) {
            for (String windowName : configuration.getWindowNames()) {
                addHistogram(namePrefix, metrics, collectorName, windowName, monitor);
                addPercentageGauge(namePrefix, metrics, collectorName, windowName, monitor);
                addGcDurationGauge(namePrefix, metrics, collectorName, windowName, monitor);
            }
        }
        return () -> metrics;
    }

    private static void addHistogram(String namePrefix, Map<String, Metric> metrics, String collectorName, String windowName, GcMonitor monitor) {
        Histogram histogram = monitor.getPauseLatencyHistogram(collectorName, windowName);
        String name = namePrefix + "." + collectorName + "." + windowName + "." + "pauseLatencyMillis";
        metrics.put(name, histogram);
    }

    private static void addGcDurationGauge(String namePrefix, Map<String, Metric> metrics, String collectorName, String windowName, GcMonitor monitor) {
        Gauge<Long> durationGauge = () -> monitor.getMillisSpentInGc(collectorName, windowName);
        String name = namePrefix + "." + collectorName + "." + windowName + "." + "millisSpentInPause";
        metrics.put(name, durationGauge);
    }

    private static void addPercentageGauge(String namePrefix, Map<String, Metric> metrics, String collectorName, String windowName, GcMonitor monitor) {
        Gauge<BigDecimal> durationGauge = () -> {
            double pausePercentage = monitor.getPausePercentage(collectorName, windowName);
            return Formatter.roundToDigits(pausePercentage, monitor.getConfiguration().getDecimalPoints());
        };
        String name = namePrefix + "." + collectorName + "." + windowName + "." + "pausePercentage";
        metrics.put(name, durationGauge);
    }

}
