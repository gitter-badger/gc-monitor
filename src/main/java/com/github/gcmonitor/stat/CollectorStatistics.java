package com.github.gcmonitor.stat;

import com.github.gcmonitor.GcMonitorConfiguration;

import java.util.Arrays;

public class CollectorStatistics implements PrettyPrintable {

    private final CollectorStatisticsWindow[] windows;

    public CollectorStatistics(GcMonitorConfiguration configuration) {
        long[] timeWindows = configuration.getTimeWindows();
        this.windows = new CollectorStatisticsWindow[timeWindows.length];
        for (int i = 0; i < windows.length; i++) {
            windows[i] = new CollectorStatisticsWindow(timeWindows[i], configuration);
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

    @Override
    public void printItself(StringBuilder builder, String indent) {
        builder.append(indent + "CollectorStatistics{");
        for (CollectorStatisticsWindow window : windows) {
            builder.append("\n");
            window.printItself(builder, indent + "\t");
        }
        builder.append("\n" + indent + "}");
    }

}
