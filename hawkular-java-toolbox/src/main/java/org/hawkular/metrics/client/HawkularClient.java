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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import org.hawkular.metrics.client.config.HawkularClientInfo;
import org.hawkular.metrics.client.model.AvailabilityMetric;
import org.hawkular.metrics.client.model.Counter;
import org.hawkular.metrics.client.model.Gauge;
import org.hawkular.metrics.client.model.Logger;
import org.hawkular.metrics.client.model.Metric;
import org.hawkular.metrics.client.model.MetricChangeListener;
import org.hawkular.metrics.client.model.Tags;
import org.hawkular.metrics.client.model.Watch;
import org.hawkular.metrics.client.monitor.MonitoringSession;

/**
 * Toolbox' Hawkular client that allows various operations related to an Hawkular server.<br/>
 * Currently only focused on Metrics, but it could also be used to interact with Alerts and Inventory in the future.
 */
public class HawkularClient {

    private final HawkularClientInfo info;
    private final MetricsNotifier metricsNotifier;
    private final MetricsTagger metricsTagger;
    private final Map<String, Counter> counters = new HashMap<>();
    private final Map<String, Gauge> gauges = new HashMap<>();
    private final Map<String, Watch> watches = new HashMap<>();
    private final Map<String, AvailabilityMetric> avails = new HashMap<>();
    private final Map<String, Logger> loggers = new HashMap<>();

    /**
     * Use {@link HawkularFactory} or {@link HawkularClientBuilder} for public construction
     */
    HawkularClient(HawkularClientInfo info) {
        this.info = info;
        metricsNotifier = new MetricsNotifier(info);
        metricsTagger = new MetricsTagger(info);
    }

    private <T extends Metric> T metric(String name,
                                        Tags tags,
                                        Map<String, T> pool,
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

    /**
     * Create a new {@link Gauge} with the given name
     */
    public Gauge gauge(String name) {
        return metric(name, Tags.empty(), gauges, Gauge::new);
    }

    /**
     * Create a new {@link Gauge} with the given name and tags
     */
    public Gauge gauge(String name, Tags tags) {
        return metric(name, tags, gauges, Gauge::new);
    }

    /**
     * Create a new {@link Counter} with the given name
     */
    public Counter counter(String name) {
        return metric(name, Tags.empty(), counters, Counter::new);
    }

    /**
     * Create a new {@link Counter} with the given name and tags
     */
    public Counter counter(String name, Tags tags) {
        return metric(name, tags, counters, Counter::new);
    }

    /**
     * Create a new {@link Watch} with the given name
     */
    public Watch watch(String name) {
        return metric(name, Tags.empty(), watches, Watch::new);
    }

    /**
     * Create a new {@link Watch} with the given name and tags
     */
    public Watch watch(String name, Tags tags) {
        return metric(name, tags, watches, Watch::new);
    }

    /**
     * Create a new {@link AvailabilityMetric} with the given name
     */
    public AvailabilityMetric availability(String name) {
        return metric(name, Tags.empty(), avails, AvailabilityMetric::new);
    }

    /**
     * Create a new {@link AvailabilityMetric} with the given name and tags
     */
    public AvailabilityMetric availability(String name, Tags tags) {
        return metric(name, tags, avails, AvailabilityMetric::new);
    }

    /**
     * Create a new {@link Logger} with the given name
     */
    public Logger logger(String name) {
        return metric(name, Tags.empty(), loggers, Logger::new);
    }

    /**
     * Create a new {@link Logger} with the given name and tags
     */
    public Logger logger(String name, Tags tags) {
        return metric(name, tags, loggers, Logger::new);
    }

    public MonitoringSession.Builder prepareMonitoringSession(long frequency, TimeUnit timeUnit) {
        return new MonitoringSession.Builder(this, frequency, timeUnit);
    }

    /**
     * Starting point for building a metric (Gauge, Counter etc.) using a {@link MetricBuilder}, which offers a convenient way
     * of tagging
     */
    public MetricBuilder metricBuilder() {
        return new MetricBuilder(this);
    }

    public HawkularClientInfo getInfo() {
        return info;
    }
}
