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

import java.util.HashMap;
import java.util.Map;

import org.hawkular.metrics.client.common.HawkularClientConfig;

/**
 * @author Joel Takvorian
 */
public class HawkularYamlConfig implements HawkularClientConfig {

    private String uri;
    private String tenant;
    private String prefix;
    private String bearerToken;
    private String username;
    private String password;
    private Map<String, String> headers;
    private Map<String, String> globalTags = new HashMap<>();
    private Map<String, Map<String, String>> perMetricTags;
    private Integer failOverCacheMaxSize;
    private Long failoverCacheDuration;

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Map<String, String> getGlobalTags() {
        return globalTags;
    }

    public void setGlobalTags(Map<String, String> globalTags) {
        this.globalTags.putAll(globalTags);
    }

    @Override
    public Map<String, Map<String, String>> getPerMetricTags() {
        return perMetricTags;
    }

    public void setPerMetricTags(Map<String, Map<String, String>> perMetricTags) {
        this.perMetricTags = perMetricTags;
    }

    @Override
    public Integer getFailoverCacheMaxSize() {
        return failOverCacheMaxSize;
    }

    public void setFailoverCacheMaxSize(Integer failOverCacheMaxSize) {
        this.failOverCacheMaxSize = failOverCacheMaxSize;
    }

    @Override
    public Long getFailoverCacheDuration() {
        return failoverCacheDuration;
    }

    public void setFailoverCacheDuration(Long failoverCacheDuration) {
        this.failoverCacheDuration = failoverCacheDuration;
    }
}
