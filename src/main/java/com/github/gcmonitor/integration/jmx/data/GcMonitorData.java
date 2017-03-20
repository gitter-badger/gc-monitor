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

import com.github.gcmonitor.stat.GcMonitorConfiguration;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcMonitorDataType;
import com.github.gcmonitor.stat.CollectorStatistics;

import java.util.HashMap;
import java.util.Map;

public class GcMonitorData extends KeyValueData {

    public GcMonitorData(GcMonitorDataType type, Map<String, CollectorStatistics> statistics, GcMonitorConfiguration configuration) {
        super(createMonitorValues(type, statistics, configuration), type);
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

}
