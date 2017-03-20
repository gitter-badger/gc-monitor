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

package com.github.gcmonitor.stat;

import com.codahale.metrics.Histogram;
import com.github.rollingmetrics.counter.WindowCounter;

import java.time.Duration;
import java.util.Optional;

public class CollectorWindow implements PrettyPrintable {

    private final long creationTimeMillis;
    private final Optional<Duration> windowDuration;
    private final WindowCounter counter;
    private final Histogram histogram;

    public CollectorWindow(long creationTimeMillis, Optional<Duration> windowDuration, WindowCounter counter, Histogram histogram) {
        this.creationTimeMillis = creationTimeMillis;
        this.windowDuration = windowDuration;
        this.counter = counter;
        this.histogram = histogram;
    }

    public void update(long collectionTimeDeltaMillis, long collectionCountDelta) {
        counter.add(collectionTimeDeltaMillis);
        long averageTimeMillis = collectionTimeDeltaMillis / collectionCountDelta;
        for (int i = 0; i < collectionCountDelta; i++) {
            histogram.update(averageTimeMillis);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        printItself(sb, "");
        return sb.toString();
    }

    @Override
    public void printItself(StringBuilder sb, String indent) {
        sb.append(indent + "CollectorWindow{");
        sb.append("\n" + indent + "\twindowDurationSeconds=").append(windowDuration.map(duration -> "" + duration.getSeconds()).orElse("uniform"));
        sb.append("\n" + indent + "\tmillisSpentInGc=").append(counter.getSum());
        sb.append("\n" + indent + "\thistogram=").append(histogram.getSnapshot().toString());
        sb.append("\n" + indent + '}');
    }

}
