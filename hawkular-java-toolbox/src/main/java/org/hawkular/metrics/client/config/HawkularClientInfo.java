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
import org.hawkular.metrics.client.model.Tags;

/**
 * @author Joel Takvorian
 */
public class HawkularClientInfo {

    private final HawkularHttpClient httpClient;
    private final String tenant;
    private final String uri;
    private final Optional<Credential> basicAuthCredential;
    private final Optional<String> bearerToken;
    private final Optional<String> prefix;
    private final Tags globalTags;
    private final Map<String, Tags> perMetricTags;
    private final Collection<RegexTags> regexTags;

    public HawkularClientInfo(
            HawkularHttpClient httpClient,
            String tenant,
            String uri, Optional<Credential> basicAuthCredential,
            Optional<String> bearerToken,
            Optional<String> prefix,
            Tags globalTags,
            Map<String, Tags> perMetricTags,
            Collection<RegexTags> regexTags) {
        this.httpClient = httpClient;
        this.tenant = tenant;
        this.uri = uri;
        this.basicAuthCredential = basicAuthCredential;
        this.bearerToken = bearerToken;
        this.prefix = prefix;
        this.globalTags = globalTags;
        this.perMetricTags = perMetricTags;
        this.regexTags = regexTags;
    }

    public String getTenant() {
        return tenant;
    }

    public String getUri() {
        return uri;
    }

    public HawkularHttpClient getHttpClient() {
        return httpClient;
    }

    public Optional<Credential> getBasicAuthCredential() {
        return basicAuthCredential;
    }

    public Optional<String> getBearerToken() {
        return bearerToken;
    }

    public Optional<String> getPrefix() {
        return prefix;
    }

    public Tags getGlobalTags() {
        return globalTags;
    }

    public Map<String, Tags> getPerMetricTags() {
        return perMetricTags;
    }

    public Collection<RegexTags> getRegexTags() {
        return regexTags;
    }
}
