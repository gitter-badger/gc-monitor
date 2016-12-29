package com.github.gcmonitor.stat;

import java.util.Arrays;

/**
 * Created by vladimir.bukhtoyarov on 28.12.2016.
 */
public class CollectorStatistics {

    private final CollectorStatisticsWindow[] windows;

    public CollectorStatistics(long[] timeWindows, double[] percentiles) {
        this.windows = new CollectorStatisticsWindow[timeWindows.length];
        for (int i = 0; i < windows.length; i++) {
            windows[i] = new CollectorStatisticsWindow(i, percentiles);
        }
    }

    public CollectorStatisticsWindow[] getWindows() {
        return windows;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CollectorStatistics{");
        sb.append("windows=").append(Arrays.toString(windows));
        sb.append('}');
        return sb.toString();
    }

}
