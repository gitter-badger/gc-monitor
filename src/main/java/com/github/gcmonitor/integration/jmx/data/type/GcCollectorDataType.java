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

package com.github.gcmonitor.integration.jmx.data.type;

import com.github.gcmonitor.stat.GcMonitorConfiguration;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import java.util.HashMap;
import java.util.Map;

public class GcCollectorDataType extends CompositeType {

    public static final String TYPE_NAME = GcMonitorDataType.TYPE_NAME + ".collector";
    private static final String DESCRIPTION = "Shows aggregated information about particular garbage collector.";

    private final Map<Long, String> windowsByDuration;
    private final GcCollectorWindowDataType windowType;

    public static GcCollectorDataType buildCompositeType(GcMonitorConfiguration configuration) {
        long[] timeWindows = configuration.getWindowSpecifications();

        Map<Long, String> namesByDuration = new HashMap<>();
        String[] itemNames = new String[timeWindows.length];
        String[] itemDescriptions = new String[timeWindows.length];
        OpenType<?>[] itemTypes = new OpenType<?>[timeWindows.length];

        GcCollectorWindowDataType windowType = GcCollectorWindowDataType.buildCompositeType(configuration);
        for (int i = 0; i < timeWindows.length; i++) {
            long durationSeconds = timeWindows[i];
            String windowName = durationSeconds + " second window";
            namesByDuration.put(durationSeconds, windowName);
            itemNames[i] = windowName;
            itemDescriptions[i] = windowName;
            itemTypes[i] = windowType;
        }

        try {
            return new GcCollectorDataType(itemNames, itemDescriptions, itemTypes, windowType, namesByDuration);
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getWindowName(long duration) {
        return windowsByDuration.get(duration);
    }

    public GcCollectorWindowDataType getWindowType() {
        return windowType;
    }

    public GcCollectorDataType(String[] itemNames, String[] itemDescriptions, OpenType<?>[] itemTypes,
                               GcCollectorWindowDataType windowType, Map<Long, String> windowsByDuration) throws OpenDataException {
        super(TYPE_NAME, DESCRIPTION, itemNames, itemDescriptions, itemTypes);
        this.windowType = windowType;
        this.windowsByDuration = windowsByDuration;
    }

}
