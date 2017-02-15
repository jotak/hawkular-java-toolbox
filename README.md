# hawkular-java-toolbox

This is a client-side toolbox for common and easy manipulation of Hawkular metrics.

## Build maven module

For now, _hawkular-java-toolbox_ is only available from this GitHub repository. You have to clone it and build it from sources:

```bash
mvn clean install
```

Then add it as a maven module:

```xml
    <dependency>
      <groupId>org.hawkular.metrics</groupId>
      <artifactId>hawkular-java-toolbox</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>
```

## Initialization

### With a builder

```java
    HawkularClient hawkular = new HawkularClientBuilder("my-tenant")
            // .[add more builder configuration here: uri, authentication, tags, ...]
            .build();
```

### With a YAML file

Configuration can be externalized in a YAML file:

```yaml
tenant: jotak
prefix: ${host}.
globalTags:
  hostname: ${host}
  who: jotak
```

It's not an exhaustive example, you can also setup uri, authentication, etc. Full list of config keys are not yet documented but you can refer to _src/main/java/org/hawkular/metrics/client/config/HawkularYamlConfig.java_
All in all it's similar to the _HawkularClientBuilder_ class, which has a correct javadoc.

_${host}_ will be automatically resolved as the hostname. It's currently the only possible variable, though more may come in the future.

Then, in your code:

```java
    HawkularClient hawkular = HawkularFactory.load().create();
```

This will pick up the YAML file given by system property _"hawkular.java.toolbox.config"_ - or, by default, an _"hawkular.yaml"_ file placed in the current working directory.

To specify a different file, use this:

```java
    HawkularClient hawkular = HawkularFactory.loadFrom("/another/file.yaml").create();
```

With _HawkularFactory_, you can also initiate a setup from the YAML file as seen above, and continue to refine it through the programmatic builder. For example:

```java
    HawkularClient hawkular = HawkularFactory.load().builder()
            .addHeader("some-special-header", "value")
            .build();
```

## Create and feed metrics

Metric type are:

- Gauges (same as in Hawkular metrics on the server side)
- Counters (same as in Hawkular metrics on the server side)
- Availability (up/down/unknown, same as in Hawkular metrics on the server side)
- Loggers (same as "String" on the server side)
- Watches (basically, a Gauge with some incorporated timing methods)

Example, getting data from a service:

```java
    Watch responseTime = hawkular.watch("myservice.response-time");
    Counter counter = hawkular.counter("myservice.count");

    // Then, somewhere in an algorithm:
    responseTime.reset();
    try {
        myService.getMyData();
        counter.inc();
    } finally {
        responseTime.tick();
    }
```

## Tagging

Tagging metrics is important to make them easier to query. Every metric factory methods from _HawkularClient_ has an overloaded version that accepts tags.

Another option is to use the _MetricBuilder_, which conveniently add tags automatically when you declare "segments" of your metric name.

For example:

```java
    Counter readFromCache = hawkular.metricBuilder()
                    .addSegment("impl", "ehcache")
                    .addSegment("source", "cache")
                    .addSegment("metric", "read")
                    .toCounter();
```

It will produce a Counter named _ehcache.cache.read_ (with eventually a prefix before),
and tagged _impl:ehcache, source:cache, metric:read_. _MetricBuilder_ is flexible enough to also allow segmenting without tags, or tagging without segments.

## Logging

There's two kind of loggers:

- the simple _Logger_ metric type, which is barely a "String" metric that can be feed in  a completely custom way.
- and the _HawkularLogger_ class, that is shamelessly inspired from existing Java logging frameworks (like log4j), but with the particularity of being tied to Hawkular, as you had guessed.

You can create an _HawkularLogger_ from the _HawkularFactory_ described above:

```java
    private static final HawkularLogger HWK = HawkularFactory.load().logger(MyClass.class);
```

What it does is it creates several _Loggers_ and _Counters_ metrics named _"MyClass.[debug/info/warning/error].logs"_ and _"MyClass.[debug/info/warning/error].count"_.

Then, events are logged like with any common logger:

```java
    try {
        HWK.info("Starting scenario for " + name);
    } catch (Exception e) {
        HWK.error(e);
    }
```

There is currently no way to specify a log level. That may come in the future.

## Monitoring sessions

Monitoring sessions is a way to programmatically start and stop a collection of pre-defined metric feeds, running on dedicated threads, executed at a defined interval.

As of now there's two sets of feeds that come with the toolbox: _CPUMonitoring_ and _MemoryMonitoring_. They are quite incomplete, but it's easy to add more.

Example of usage:

```java
    hawkular.prepareMonitoringSession(1, TimeUnit.SECONDS)
            .feeds(CPUMonitoring.create())  // Will feed metric "monitor.cpu.core"
            .feeds(MemoryMonitoring.create()) // Will feed metrics "monitor.memory.system.free", "monitor.memory.system.swap.free" and "monitor.memory.process.heap"
            .run(() -> myAlgorithmToMonitor());
```

## Exporting to Grafana

The toolbox can open connections to a running grafana server and send programmatically created dashboards.

This feature lies in a separate module:

```xml
    <dependency>
      <groupId>org.hawkular.metrics</groupId>
      <artifactId>hawkular-java-toolbox-grafana-exporter</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>
```

The first step is to configure the connection to Grafana. An API key is needed, with edit rights at least.

```java
    GrafanaConnection grafanaConnection = new GrafanaConnection("the-api-key");
```

It is possible (although not necessary) to push the Hawkular configuration, as defined in the YAML file or through _HawkularClientBuilder_, as a new datasource in Grafana:

```java
    grafanaConnection.createOrUpdateDatasource(GrafanaDatasource.fromHawkularConfig("my-datasource", hawkular.getInfo()));
```

But it requires admin rights to the API key. Otherway, you can just create it manually from the Grafana UI.

Then, create and export dashboards to Grafana. For example, a dashboard created all at once, taking advantage of tags:
```java
    GrafanaDashboard dashboardAllAtOnce = new GrafanaDashboard()
            .title("dashboard1")
            .addAnnotation(new GrafanaAnnotation("Errors", "BackendMonitoring.error.logs")
                    .color("red"))
            .addAnnotation(new GrafanaAnnotation("Warnings", "BackendMonitoring.warning.logs")
                    .color("orange"))
            .addAnnotation(new GrafanaAnnotation("Info", "BackendMonitoring.info.logs")
                    .color("blue"))
            .addRow(new GrafanaRow()
                    .addPanel(GrafanaPanel.graph()
                            .title("Storage size")
                            .addTarget(GrafanaTarget.gaugesTagged("metric", "size")))
                    .addPanel(GrafanaPanel.graph()
                            .title("Read response time")
                            .addTarget(GrafanaTarget.gaugesTagged("metric", "response-time"))))
            .addRow(new GrafanaRow()
                    .addPanel(GrafanaPanel.graph()
                            .title("Read cache vs DB (mean rate)"))
                    .addPanel(GrafanaPanel.graph()
                            .title("Read cache vs DB (count)")
                            .addTarget(GrafanaTarget.countersTagged("metric", "read"))));

    grafanaConnection.sendDashboard(new GrafanaDashboardMessage(dashboardAllAtOnce, "my-datasource"));
```

It's also possible the add a specific metric to a dashboard: 

```java
    Gauge sizeGauge = hawkular.gauge("size");
    dashboard.addRow(new GrafanaRow()
            .addPanel(GrafanaPanel.graph().title("storage size").addTarget(GrafanaTarget.fromMetric(sizeGauge)))
    // etc.
    grafanaConnection.sendDashboard(new GrafanaDashboardMessage(dashboard, "my-datasource"));
```

Also, an _HawkularLogger_ can be directly exported as annotations:
```java
    dashboard.addLoggerAnnotations(HWK);
```

## Bridge with dropwizard

There's obviously some overlap between this toolbox and [Dropwizard metrics](http://metrics.dropwizard.io/3.1.0/). Hawkular already has its [Dropwizard reporter](https://github.com/hawkular/hawkular-dropwizard-reporter).

If you already use dropwizard and want to use this toolbox additionally, you may want to report the dropwizard metrics to Hawkular as well. In order to do so, this maven depdency must be added:

```xml
    <dependency>
      <groupId>org.hawkular.metrics</groupId>
      <artifactId>hawkular-java-toolbox-dropwizard</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>
```

Then, bind them together. Example: 
```java
    HawkularDropwizardBinder
            .fromRegistry(dropwizardRegistry)
            .withTag("source", "dropwizard")
            .bindWith(hawkular.getInfo(), 1, TimeUnit.SECONDS);
```

It will _in fine_ create an Hawkular Dropwizard Reporter with the same configuration as this Hawkular toolbox.
