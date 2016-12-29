package com.github.gcmonitor.integration.jmx.data.type;

import com.github.gcmonitor.GcMonitorConfiguration;
import com.github.gcmonitor.stat.CollectorStatistics;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

public class GcCollectorWindowDataType extends CompositeType {

    public static final String TYPE_NAME = GcCollectorDataType.TYPE_NAME + ".window";
    public static final String DESCRIPTION = "Shows information about particular garbage collector aggregated by time window.";

    public static final String HISTOGRAM_ITEM = "histogram";
    public static final String UTILIZATION_ITEM = "utilization";

    private final GcLatencyHistogramDataType histogramType;

    public static GcCollectorWindowDataType buildCompositeType(GcMonitorConfiguration configuration) {
        GcLatencyHistogramDataType histogramType = GcLatencyHistogramDataType.buildCompositeType(configuration);
        try {
            return new GcCollectorWindowDataType(histogramType);
        } catch (OpenDataException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public UtilizationDataType getUtilizationType() {
        return UtilizationDataType.INSTANCE;
    }

    public GcLatencyHistogramDataType getHistogramType() {
        return histogramType;
    }

    public GcCollectorWindowDataType(GcLatencyHistogramDataType histogramType) throws OpenDataException {
        super(TYPE_NAME,
                DESCRIPTION,
                new String[] {
                        HISTOGRAM_ITEM,
                        UTILIZATION_ITEM
                },
                new String[] {
                        "GC latency histogram",
                        "Collector utilization"
                },
                new OpenType<?>[] {
                        histogramType,
                        UtilizationDataType.INSTANCE
                }
        );
        this.histogramType = histogramType;
    }

}
