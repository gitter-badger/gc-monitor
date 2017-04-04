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
The library is in the middle of testing and documentation. First official stable release is planed to April 2017.

# For the impatient adopters
If you want to try early then use alpha build(or just build from sources by yourself)
```xml
<!-- gc-monitor itself -->
<dependency>
    <groupId>io.github.gc-monitor</groupId>
    <artifactId>gc-monitor</artifactId>
    <version>1.0.0-alpha</version>
</dependency>

<!-- Third-party dependencies required by gc-monitor -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>rolling-metrics</artifactId>
    <version>2.0.2</version>
</dependency>
<dependency>
    <groupId>io.dropwizard.metrics</groupId>
    <artifactId>metrics-core</artifactId>
    <version>3.1.2</version>
</dependency>
<dependency>
    <groupId>org.hdrhistogram</groupId>
    <artifactId>HdrHistogram</artifactId>
    <version>2.1.8</version>
</dependency>
```
To get this alpha build it is need to add following repository: 
```xml
<repository>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
    <id>gc-monitor-bintray-repo</id>
    <name>gc-monitor-bintray-repo</name>
    <url>http://dl.bintray.com/vladimir-bukhtoyarov/maven</url>
</repository>
```