package com.github.gcmonitor.integration.jmx.data.type;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

public class UtilizationDataType extends CompositeType {

    public static final String TYPE_NAME = GcCollectorWindowDataType.TYPE_NAME + ".utilization";
    public static final String DESCRIPTION = "Shows information about collector utilization.";

    public static final UtilizationDataType INSTANCE; static {
        try {
            INSTANCE = new UtilizationDataType();
        } catch (OpenDataException e) {
            throw new IllegalStateException(e);
        }
    }

    private UtilizationDataType() throws OpenDataException {
        super(TYPE_NAME,
                DESCRIPTION,
                new String[] {
                    "stwDurationMillis",
                    "stwPercentage"
                },
                new String[] {
                    "Time in milliseconds which JVM spent in STW GC pauses instead of doing real work",
                    "Percentage of time which JVM spent in STW GC pauses instead of doing real work",
                },
                new OpenType<?>[] {
                        SimpleType.LONG,
                        SimpleType.BIGDECIMAL
                }
        );
    }

}
