/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.metrics.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import org.hawkular.metrics.client.config.HawkularClientInfo;
import org.hawkular.metrics.client.model.AvailabilityMetric;
import org.hawkular.metrics.client.model.Counter;
import org.hawkular.metrics.client.model.Gauge;
import org.hawkular.metrics.client.model.Metric;
import org.hawkular.metrics.client.model.MetricChangeListener;
import org.hawkular.metrics.client.model.Timeline;
import org.hawkular.metrics.client.model.Watch;

public class HawkularClient {

    private final HawkularClientInfo info;
    private final MetricsNotifier metricsNotifier;
    private final MetricsTagger metricsTagger;
    private final Map<String, Counter> counters = new HashMap<>();
    private final Map<String, Gauge> gauges = new HashMap<>();
    private final Map<String, Watch> watches = new HashMap<>();
    private final Map<String, AvailabilityMetric> avails = new HashMap<>();
    private final Map<String, Timeline> timelines = new HashMap<>();

    HawkularClient(HawkularClientInfo info) {
        this.info = info;
        metricsNotifier = new MetricsNotifier(info);
        metricsTagger = new MetricsTagger(info);
    }

    public <T extends Metric> T metric(String name,
                                          Map<String, String> tags, Map<String, T> pool,
                                          BiFunction<String, MetricChangeListener, T> factory) {
        String fullname = info.getPrefix().map(p -> p + name).orElse(name);
        if (pool.containsKey(fullname)) {
            return pool.get(fullname);
        }
        T metric = factory.apply(fullname, metricsNotifier);
        pool.put(fullname, metric);
        metricsTagger.tagMetric(metric, tags);
        return metric;
    }

    public Gauge gauge(String name) {
        return metric(name, Collections.emptyMap(), gauges, Gauge::new);
    }

    public Gauge gauge(String name, Map<String, String> tags) {
        return metric(name, tags, gauges, Gauge::new);
    }

    public Counter counter(String name) {
        return metric(name, Collections.emptyMap(), counters, Counter::new);
    }

    public Counter counter(String name, Map<String, String> tags) {
        return metric(name, tags, counters, Counter::new);
    }

    public Watch watch(String name) {
        return metric(name, Collections.emptyMap(), watches, Watch::new);
    }

    public Watch watch(String name, Map<String, String> tags) {
        return metric(name, tags, watches, Watch::new);
    }

    public AvailabilityMetric availability(String name) {
        return metric(name, Collections.emptyMap(), avails, AvailabilityMetric::new);
    }

    public AvailabilityMetric availability(String name, Map<String, String> tags) {
        return metric(name, tags, avails, AvailabilityMetric::new);
    }

    public Timeline timeline(String name) {
        return metric(name, Collections.emptyMap(), timelines, Timeline::new);
    }

    public Timeline timeline(String name, Map<String, String> tags) {
        return metric(name, tags, timelines, Timeline::new);
    }

    public void warn(String message) {
        Map<String, String> tags = Collections.singletonMap("severity", "warning");
        counter("warning.count", tags).inc();
        timeline("warning.timeline", tags).set(message);
    }

    public void warn(String message, Map<String, String> dpTags) {
        Map<String, String> tags = Collections.singletonMap("severity", "warning");
        counter("warning.count", tags).inc();
        timeline("warning.timeline", tags).set(message, dpTags);
    }

    public void error(String message) {
        Map<String, String> tags = Collections.singletonMap("severity", "error");
        counter("error.count", tags).inc();
        timeline("error.timeline", tags).set(message);
    }

    public void error(String message, Map<String, String> dpTags) {
        Map<String, String> tags = Collections.singletonMap("severity", "error");
        counter("error.count", tags).inc();
        timeline("error.timeline", tags).set(message, dpTags);
    }

    public MonitoringSession.Builder prepareMonitoringSession(long frequency, TimeUnit timeUnit) {
        return new MonitoringSession.Builder(this, frequency, timeUnit);
    }

    public SegmentedMetric segmentedMetric() {
        return new SegmentedMetric(this);
    }

    public HawkularClientInfo getInfo() {
        return info;
    }

}
