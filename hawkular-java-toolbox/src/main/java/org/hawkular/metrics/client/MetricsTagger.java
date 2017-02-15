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
import java.util.Map;
import java.util.Optional;

import org.hawkular.metrics.client.common.http.HawkularHttpClient;
import org.hawkular.metrics.client.config.HawkularClientInfo;
import org.hawkular.metrics.client.model.Metric;
import org.hawkular.metrics.client.model.Tags;

/**
 * @author Joel Takvorian
 */
class MetricsTagger {

    private final Tags globalTags;
    private final Map<String, Tags> perMetricTags;
    private final Collection<RegexTags> regexTags;
    private final HawkularHttpClient hawkularClient;

    MetricsTagger(HawkularClientInfo config) {
        this.globalTags = config.getGlobalTags();
        this.perMetricTags = config.getPerMetricTags();
        this.regexTags = config.getRegexTags();
        this.hawkularClient = config.getHttpClient();
    }

    void tagMetric(Metric metric, Tags newTags) {
        Tags matchingRegexTags = regexTags.stream()
                .map(reg -> reg.match(metric.getName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Tags::empty, Tags::from, Tags::from);
        Tags allTags = Tags.from(
                globalTags,
                matchingRegexTags,
                perMetricTags.getOrDefault(metric.getName(), Tags.empty()),
                newTags);
        if (!allTags.isEmpty()) {
            hawkularClient.putTags(metric.getHawkularType(), metric.getName(), HawkularJson.tagsToString(allTags));
        }
    }
}
