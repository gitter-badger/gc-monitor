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

package io.github.gcmonitor.stat;

import com.codahale.metrics.Snapshot;

/**
 * Created by vladimir.bukhtoyarov on 20.03.2017.
 */
public class CollectorWindowSnapshot {

    private final Snapshot pauseHistogramSnapshot;
    private final long millisSpentInGc;
    private final double percentageSpentInGc;

    public CollectorWindowSnapshot(Snapshot pauseHistogramSnapshot, long millisSpentInGc, double percentageSpentInGc) {
        this.pauseHistogramSnapshot = pauseHistogramSnapshot;
        this.millisSpentInGc = millisSpentInGc;
        this.percentageSpentInGc = percentageSpentInGc;
    }

    public double getPercentageSpentInGc() {
        return percentageSpentInGc;
    }

    public long getMillisSpentInGc() {
        return millisSpentInGc;
    }

    public Snapshot getPauseHistogramSnapshot() {
        return pauseHistogramSnapshot;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CollectorWindowSnapshot{");
        sb.append("pauseHistogramSnapshot=").append(pauseHistogramSnapshot);
        sb.append(", millisSpentInGc=").append(millisSpentInGc);
        sb.append(", percentageSpentInGc=").append(percentageSpentInGc);
        sb.append('}');
        return sb.toString();
    }
}
