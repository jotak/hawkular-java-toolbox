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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hawkular.metrics.client.common.http.HawkularHttpClient;
import org.hawkular.metrics.client.config.HawkularClientInfo;
import org.hawkular.metrics.client.config.RegexTags;
import org.hawkular.metrics.client.model.Metric;

/**
 * @author Joel Takvorian
 */
class MetricsTagger {

    private final Map<String, String> globalTags;
    private final Map<String, Map<String, String>> perMetricTags;
    private final Collection<RegexTags> regexTags;
    private final HawkularHttpClient hawkularClient;

    MetricsTagger(HawkularClientInfo config) {
        this.globalTags = config.getGlobalTags();
        this.perMetricTags = config.getPerMetricTags();
        this.regexTags = config.getRegexTags();
        this.hawkularClient = config.getHttpClient();
    }

    public void tagMetric(Metric metric, Map<String, String> newTags) {
        Map<String, String> existingTags = perMetricTags.computeIfAbsent(metric.getName(), k -> new HashMap<>());
        existingTags.putAll(newTags);

        Map<String, String> tags = new LinkedHashMap<>(globalTags);
        tags.putAll(getTagsForMetrics(metric.getName()));
        if (!tags.isEmpty()) {
            hawkularClient.putTags(metric.getHawkularType(), metric.getName(), HawkularJson.tagsToString(tags));
        }
    }

    private Map<String, String> getTagsForMetrics(String name) {
        Map<String, String> tags = new LinkedHashMap<>();
        regexTags.forEach(reg -> reg.match(name).ifPresent(tags::putAll));
        if (perMetricTags.containsKey(name)) {
            tags.putAll(perMetricTags.get(name));
        }
        return tags;
    }
}
