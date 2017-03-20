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
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorWindowDataType;
import com.github.gcmonitor.stat.CollectorStatistics;

import java.util.HashMap;
import java.util.Map;

public class GcCollectorData extends KeyValueData {

    public GcCollectorData(GcCollectorDataType type, CollectorStatistics collectorStatistics, GcMonitorConfiguration configuration) {
        super(createCollectorValues(type, collectorStatistics, configuration), type);
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

}
