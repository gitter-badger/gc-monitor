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

public class CollectorWindow {

    private final long creationTimeMillis;
    private final Optional<Duration> windowDuration;
    private final WindowCounter stwMillisCounter;
    private final Histogram pauseLatencyHistogram;

    public CollectorWindow(long creationTimeMillis, Optional<Duration> windowDuration, WindowCounter stwMillisCounter, Histogram pauseLatencyHistogram) {
        this.creationTimeMillis = creationTimeMillis;
        this.windowDuration = windowDuration;
        this.stwMillisCounter = stwMillisCounter;
        this.pauseLatencyHistogram = pauseLatencyHistogram;
    }

    public void update(long collectionTimeDeltaMillis, long collectionCountDelta) {
        stwMillisCounter.add(collectionTimeDeltaMillis);
        long averageTimeMillis = collectionTimeDeltaMillis / collectionCountDelta;
        for (int i = 0; i < collectionCountDelta; i++) {
            pauseLatencyHistogram.update(averageTimeMillis);
        }
    }

    public CollectorWindowSnapshot getSnapshot(long currentTimeMillis) {
        long millisSpentInGc = stwMillisCounter.getSum();

        long millisSinceCreation = currentTimeMillis - creationTimeMillis;
        long normalizationWindow;
        if (!windowDuration.isPresent()) {
            normalizationWindow = millisSinceCreation;
        } else {
            long rollingTimeWindowMillis = windowDuration.get().toMillis();
            if (millisSinceCreation < rollingTimeWindowMillis) {
                normalizationWindow = millisSinceCreation;
            } else {
                normalizationWindow = rollingTimeWindowMillis;
            }
        }

        double percentage = (double) millisSpentInGc * 100 / normalizationWindow;
        return new CollectorWindowSnapshot(pauseLatencyHistogram.getSnapshot(), millisSpentInGc, percentage);
    }

}
