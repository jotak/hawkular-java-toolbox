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
package org.hawkular.metrics.client.grafana;

import org.hawkular.metrics.client.model.Metric;
import org.hawkular.metrics.client.model.Tag;
import org.hawkular.metrics.client.model.Tags;

/**
 * @author Joel Takvorian
 */
public class GrafanaTarget {
    private final String type;
    private String tagsQL;
    private String metricName;
    private boolean rate;

    private GrafanaTarget(String type) {
        this.type = type;
    }

    public static GrafanaTarget gauge(String metricName) {
        return new GrafanaTarget("gauge").metricName(metricName);
    }

    public static GrafanaTarget counter(String metricName) {
        return new GrafanaTarget("counter").metricName(metricName);
    }

    public static GrafanaTarget availability(String metricName) {
        return new GrafanaTarget("availability").metricName(metricName);
    }

    public static GrafanaTarget gaugesTagged(String tagsQL) {
        return new GrafanaTarget("gauge").tagsQL(tagsQL);
    }

    public static GrafanaTarget countersTagged(String tagsQL) {
        return new GrafanaTarget("counter").tagsQL(tagsQL);
    }

    public static GrafanaTarget availabilitiesTagged(String tagsQL) {
        return new GrafanaTarget("availability").tagsQL(tagsQL);
    }

    public static GrafanaTarget gaugesTagged(Tag tag) {
        return new GrafanaTarget("gauge").tagsQL(tag.toQL());
    }

    public static GrafanaTarget countersTagged(Tag tag) {
        return new GrafanaTarget("counter").tagsQL(tag.toQL());
    }

    public static GrafanaTarget availabilitiesTagged(Tag tag) {
        return new GrafanaTarget("availability").tagsQL(tag.toQL());
    }

    public static GrafanaTarget gaugesTagged(Tags tags) {
        return new GrafanaTarget("gauge").tagsQL(tags.toQL());
    }

    public static GrafanaTarget countersTagged(Tags tags) {
        return new GrafanaTarget("counter").tagsQL(tags.toQL());
    }

    public static GrafanaTarget availabilitiesTagged(Tags tags) {
        return new GrafanaTarget("availability").tagsQL(tags.toQL());
    }

    public GrafanaTarget tagsQL(String tagsQL) {
        this.tagsQL = tagsQL;
        return this;
    }

    public GrafanaTarget metricName(String metricName) {
        this.metricName = metricName;
        return this;
    }

    public GrafanaTarget rate() {
        this.rate = true;
        return this;
    }

    public static GrafanaTarget fromMetric(Metric metric) {
        return new GrafanaTarget(convertType(metric.getHawkularType()))
                .metricName(metric.getName());
    }

    private static String convertType(String type) {
        switch (type) {
            case "gauges":
                return "gauge";
            case "counters":
                return "counter";
            default:
                return "availability";
        }
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"type\":\"").append(type).append("\"");
        sb.append(", \"rate\":").append(rate);
        if (metricName != null) {
            sb.append(", \"id\":\"").append(metricName).append("\"");
        } else {
            sb.append(", \"tagsQL\":\"").append(tagsQL).append("\"");
        }
        return sb.append("}").toString();
    }
}
