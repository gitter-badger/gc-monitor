package com.github.gcmonitor.stat;

import com.codahale.metrics.Histogram;
import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.rollingmetrics.counter.SmoothlyDecayingRollingCounter;
import com.github.rollingmetrics.counter.WindowCounter;
import com.github.rollingmetrics.histogram.HdrBuilder;
import com.github.rollingmetrics.histogram.OverflowResolver;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class CollectorStatisticsWindow implements PrettyPrintable {

    private final long windowDurationSeconds;
    private final WindowCounter counter;
    private final Histogram histogram;

    CollectorStatisticsWindow(long windowDurationSeconds, GcMonitorConfiguration configuration) {
        this.windowDurationSeconds = windowDurationSeconds;
        Duration rollingWindow = Duration.ofSeconds(windowDurationSeconds);
        this.counter = new SmoothlyDecayingRollingCounter(rollingWindow, configuration.getCounterChunks());
        this.histogram = new HdrBuilder()
                .resetReservoirPeriodicallyByChunks(rollingWindow, configuration.getHistogramChunks())
                .withHighestTrackableValue(configuration.getLongestTrackablePauseMillis(), OverflowResolver.REDUCE_TO_HIGHEST_TRACKABLE)
                .withPredefinedPercentiles(configuration.getPercentiles())
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
        sb.append(", millisSpentInGc=").append(counter.getSum());
        sb.append(", histogram=").append(histogram.getSnapshot().toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public void printItself(StringBuilder sb, String indent) {
        sb.append(indent + "CollectorStatisticsWindow{");
        sb.append("\n" + indent + "\twindowDurationSeconds=").append(windowDurationSeconds);
        sb.append("\n" + indent + "\tmillisSpentInGc=").append(counter.getSum());
        sb.append("\n" + indent + "\thistogram=").append(histogram.getSnapshot().toString());
        sb.append("\n" + indent + '}');
    }
}
