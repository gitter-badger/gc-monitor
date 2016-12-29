package com.github.gcmonitor;

import java.util.concurrent.TimeUnit;

public class GcMonitorConfiguration {

    public static final int MAX_WINDOWS = 20;

    public static final long[] DEFAULT_TIME_WINDOWS = {
            TimeUnit.MINUTES.toSeconds(1),
            TimeUnit.MINUTES.toSeconds(5),
            TimeUnit.MINUTES.toSeconds(15),
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.HOURS.toSeconds(24),
    };
    public static final double[] DEFAULT_PERCENTILES = new double[] {0.5, 0.75, 0.9, 0.95, 0.98, 0.99, 0.999};

    public static final GcMonitorConfiguration DEFAULT = new GcMonitorConfiguration(DEFAULT_TIME_WINDOWS, DEFAULT_PERCENTILES);

    public static final int COUNTER_CHUNKS = 10;
    public static final int HISTOGRAM_CHUNKS = 5;
    public static final long LONGEST_TRACKABLE_PAUSE_MILLIS = TimeUnit.MINUTES.toMillis(15);

    private final long[] timeWindows;
    private final double[] percentiles;

    public GcMonitorConfiguration(long[] timeWindows, double[] percentiles) {
        validateTimeWindows(timeWindows);
        this.timeWindows = timeWindows.clone();
        this.percentiles = percentiles.clone();
    }

    public double[] getPercentiles() {
        return percentiles;
    }

    public long[] getTimeWindows() {
        return timeWindows;
    }

    public int getCounterChunks() {
        return COUNTER_CHUNKS;
    }

    public int getHistogramChunks() {
        return HISTOGRAM_CHUNKS;
    }

    public long getLongestTrackablePauseMillis() {
        return LONGEST_TRACKABLE_PAUSE_MILLIS;
    }

    private static void validateTimeWindows(long[] timeWindows) {
        // TODO
    }

}
