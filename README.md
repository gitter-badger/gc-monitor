# gc-monitor
Set of advanced monitoring metrics for OpenJDK Garbage collector.

The main feature of ```gc-monitor``` is ability to work without configuration of GC logging at JVM level, ```gc-monitor``` works fine without any options like ```-XX:+PrintGC```,
just add ```gc-monitor``` to your application and need not to configure anything.


## Statistics provided by gc-monitor
- **GC pause histogram** - min, max, average, percentiles, standard deviation.
- **GC utilization** - time in percentage for which JVM will be stopped in stop the world pauses caused by garbage collection.

All statistics organized into rolling time windows, the windows are configurable.

## Reporters
- [Dropwizard/Metrics](http://metrics.dropwizard.io/3.2.2/)
- [JMX](http://www.oracle.com/technetwork/articles/java/javamanagement-140525.html)

# Project state
The library is under development. First release is proposed to April 2017.