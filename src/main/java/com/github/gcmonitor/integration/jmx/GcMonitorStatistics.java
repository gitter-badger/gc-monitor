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

package com.github.gcmonitor.integration.jmx;

import com.github.gcmonitor.GcMonitor;
import com.github.gcmonitor.integration.jmx.data.GcMonitorData;
import com.github.gcmonitor.integration.jmx.data.type.GcMonitorDataType;


public class GcMonitorStatistics implements GcMonitorStatisticsMXBean {

    private final GcMonitor gcMonitor;
    private final GcMonitorDataType type;

    public GcMonitorStatistics(GcMonitor gcMonitor) {
        this.gcMonitor = gcMonitor;
        this.type = GcMonitorDataType.buildCompositeType(gcMonitor.getCollectorNames(), gcMonitor.getConfiguration());
    }

    @Override
    public GcMonitorData getGcMonitorData() {
        try {
            GcMonitorData[] dataRef = new GcMonitorData[1];
            gcMonitor.getStatistics(statistics -> dataRef[0] = new GcMonitorData(type, statistics, gcMonitor.getConfiguration()));
            GcMonitorData result = dataRef[0];
            return result;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

}
