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
import com.github.gcmonitor.integration.jmx.converter.SnapshotConverter;
import com.github.gcmonitor.stat.GcMonitorSnapshot;

import javax.management.openmbean.*;

/**
 * TODO add javadoc
 */
public class GcStatistics implements GcStatisticsMBean {

    private final GcMonitor gcMonitor;
    private final SnapshotConverter snapshotConverter;

    public GcStatistics(GcMonitor gcMonitor) {
        this.gcMonitor = gcMonitor;
        this.snapshotConverter = new SnapshotConverter(gcMonitor.getConfiguration());
    }

    @Override
    public CompositeData getGcMonitorData() {
        GcMonitorSnapshot snapshot = gcMonitor.getSnapshot();
        return snapshotConverter.map(snapshot);
    }

}
