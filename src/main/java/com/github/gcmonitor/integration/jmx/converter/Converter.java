package com.github.gcmonitor.integration.jmx.converter;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;

public interface Converter<S> {

    CompositeData map(S source);

    CompositeType getType();

}
