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
import com.github.gcmonitor.integration.jmx.data.type.GcCollectorDataType;
import com.github.gcmonitor.integration.jmx.data.type.GcMonitorDataType;
import com.github.gcmonitor.stat.GcMonitorConfiguration;
import com.github.gcmonitor.stat.GcMonitorSnapshot;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO add javadoc
 */
public class GcMonitorStatistics implements GcMonitorStatisticsMBean {

    public static final String GC_MONITOR_TYPE_NAME = "com.github.gcmonitor.data";
    public static final String GC_MONITOR_TYPE_DESCRIPTION = "Shows aggregated information about garbage collectors.";

    private final GcMonitor gcMonitor;
    private final CompositeType type;
    private final Map<String, CompositeType> collectorTypes;
    private final Map<String, Map<String, CompositeType>> collectorWindowTypes;

    /**
     * TODO add javadoc
     *
     * @param gcMonitor
     */
    public GcMonitorStatistics(GcMonitor gcMonitor) {
        this.gcMonitor = gcMonitor;
        this.type = buildGcMonitorCompositeType(gcMonitor);
    }

    @Override
    public CompositeData getGcMonitorData() {
        GcMonitorSnapshot snapshot = gcMonitor.getSnapshot();
        return buildGcMonitorData(snapshot);
    }

    private static CompositeType buildGcMonitorCompositeType(GcMonitor gcMonitor) {
        GcMonitorConfiguration configuration = gcMonitor.getConfiguration();
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

    private CompositeData buildGcMonitorData(GcMonitorSnapshot snapshot) {
        // TODO
        return null;
    }

}
