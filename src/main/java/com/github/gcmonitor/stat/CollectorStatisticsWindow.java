package com.github.gcmonitor.stat;

import com.codahale.metrics.Histogram;
import com.github.gcmonitor.GcMonitor;
import com.github.rollingmetrics.counter.SmoothlyDecayingRollingCounter;
import com.github.rollingmetrics.counter.WindowCounter;
import com.github.rollingmetrics.histogram.HdrBuilder;
import com.github.rollingmetrics.histogram.OverflowResolver;

import java.time.Duration;

public class CollectorStatisticsWindow {

    private final long windowDurationSeconds;
    private final WindowCounter counter;
    private final Histogram histogram;

    public CollectorStatisticsWindow(long windowDurationSeconds) {
        this.windowDurationSeconds = windowDurationSeconds;
        Duration rollingWindow = Duration.ofSeconds(windowDurationSeconds);
        this.counter = new SmoothlyDecayingRollingCounter(rollingWindow, GcMonitor.COUNTER_CHUNKS);
        this.histogram = new HdrBuilder()
                .resetReservoirPeriodicallyByChunks(rollingWindow, GcMonitor.HISTOGRAM_CHUNKS)
                .withHighestTrackableValue(GcMonitor.LONGEST_TRACKABLE_PAUSE_MILLIS, OverflowResolver.REDUCE_TO_HIGHEST_TRACKABLE)
                .buildHistogram();
    }

    public Histogram getHistogram() {
        return histogram;
    }

    public long getWindowDurationSeconds() {
        return windowDurationSeconds;
    }

    public WindowCounter getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CollectorStatisticsWindow{");
        sb.append("windowDurationSeconds=").append(windowDurationSeconds);
        sb.append(", counter=").append(counter);
        sb.append(", histogram=").append(histogram);
        sb.append('}');
        return sb.toString();
    }

}
