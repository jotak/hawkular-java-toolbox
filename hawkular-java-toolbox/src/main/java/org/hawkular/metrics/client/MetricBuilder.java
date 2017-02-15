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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.hawkular.metrics.client.model.AvailabilityMetric;
import org.hawkular.metrics.client.model.Counter;
import org.hawkular.metrics.client.model.Gauge;
import org.hawkular.metrics.client.model.Logger;
import org.hawkular.metrics.client.model.Tag;
import org.hawkular.metrics.client.model.Tags;
import org.hawkular.metrics.client.model.Watch;

/**
 * @author Joel Takvorian
 */
public class MetricBuilder {

    private final HawkularClient hawkularClient;
    private final Map<String, String> segments = new LinkedHashMap<>();
    private final Tags tags = Tags.empty();
    private String separator = ".";

    public MetricBuilder(HawkularClient hawkularClient) {
        this.hawkularClient = hawkularClient;
    }

    public MetricBuilder addSegment(String name, String value) {
        segments.put(name, value);
        tags.add(Tag.keyValue(name, value));
        return this;
    }

    public MetricBuilder addSegment(Tag tag) {
        tag.getValue().ifPresent(v -> segments.put(tag.getKey(), v));
        tags.add(tag);
        return this;
    }

    public MetricBuilder addSegments(Tags tags) {
        tags.forEach(this::addSegment);
        return this;
    }

    public MetricBuilder addTag(String key, String value) {
        tags.add(Tag.keyValue(key, value));
        return this;
    }

    public MetricBuilder addTag(Tag tag) {
        tags.add(tag);
        return this;
    }

    public MetricBuilder addTags(Tags tags) {
        tags.forEach(this::addTag);
        return this;
    }

    public MetricBuilder separator(String separator) {
        this.separator = separator;
        return this;
    }

    private String buildName() {
        return segments.values().stream().collect(Collectors.joining(separator));
    }

    public Gauge toGauge() {
        return hawkularClient.gauge(buildName(), tags);
    }

    public Counter toCounter() {
        return hawkularClient.counter(buildName(), tags);
    }

    public Watch toWatch() {
        return hawkularClient.watch(buildName(), tags);
    }

    public AvailabilityMetric toAvailability() {
        return hawkularClient.availability(buildName(), tags);
    }

    public Logger toLogger() {
        return hawkularClient.logger(buildName(), tags);
    }
}
