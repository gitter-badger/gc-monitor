package com.github.gcmonitor.integration.jmx.converter;

import com.github.gcmonitor.stat.GcMonitorSnapshot;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;


public interface Converter {

    CompositeData map(GcMonitorSnapshot snapshot);

    CompositeType getType();

}
