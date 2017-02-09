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
package org.hawkular.metrics.client.config;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.hawkular.metrics.client.RegexTags;
import org.hawkular.metrics.client.common.http.HawkularHttpClient;

/**
 * @author Joel Takvorian
 */
public class HawkularClientInfo {

    private final HawkularHttpClient httpClient;
    private final String tenant;
    private final Optional<String> prefix;
    private final Map<String, String> globalTags;
    private final Map<String, Map<String, String>> perMetricTags;
    private final Collection<RegexTags> regexTags;

    public HawkularClientInfo(
            HawkularHttpClient httpClient,
            String tenant,
            Optional<String> prefix,
            Map<String, String> globalTags,
            Map<String, Map<String, String>> perMetricTags,
            Collection<RegexTags> regexTags) {
        this.httpClient = httpClient;
        this.tenant = tenant;
        this.prefix = prefix;
        this.globalTags = globalTags;
        this.perMetricTags = perMetricTags;
        this.regexTags = regexTags;
    }

    public String getTenant() {
        return tenant;
    }

    public HawkularHttpClient getHttpClient() {
        return httpClient;
    }

    public Optional<String> getPrefix() {
        return prefix;
    }

    public Map<String, String> getGlobalTags() {
        return globalTags;
    }

    public Map<String, Map<String, String>> getPerMetricTags() {
        return perMetricTags;
    }

    public Collection<RegexTags> getRegexTags() {
        return regexTags;
    }
}
