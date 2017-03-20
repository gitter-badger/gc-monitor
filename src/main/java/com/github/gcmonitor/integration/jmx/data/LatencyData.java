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

import com.codahale.metrics.Snapshot;
import com.github.gcmonitor.stat.GcMonitorConfiguration;
import com.github.gcmonitor.integration.jmx.data.type.GcLatencyHistogramDataType;
import com.github.gcmonitor.stat.CollectorWindow;

import java.util.HashMap;
import java.util.Map;

public class LatencyData extends KeyValueData {

    public LatencyData(GcLatencyHistogramDataType type, CollectorWindow window, GcMonitorConfiguration configuration) {
        super(createLatencyValues(type, window, configuration), type);
    }

    private static Map<String, Object> createLatencyValues(GcLatencyHistogramDataType type, CollectorWindow window, GcMonitorConfiguration configuration) {
        int decimalPoints = configuration.getDecimalPoints();
        Map<String, Object> values = new HashMap<>();
        Snapshot snapshot = window.getHistogram().getSnapshot();
        type.getExtractors().forEach((key, valueExtractor) -> values.put(key, valueExtractor.apply(snapshot, decimalPoints)));
        return values;
    }

}
