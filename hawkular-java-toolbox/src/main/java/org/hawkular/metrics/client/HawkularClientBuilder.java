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

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.hawkular.metrics.client.common.HawkularClientConfig;
import org.hawkular.metrics.client.common.http.HawkularHttpClient;
import org.hawkular.metrics.client.common.http.JdkHawkularHttpClient;
import org.hawkular.metrics.client.config.HawkularClientInfo;
import org.hawkular.metrics.client.config.RegexTags;

public class HawkularClientBuilder {

    private static final String KEY_HEADER_TENANT = "Hawkular-Tenant";
    private static final String KEY_HEADER_AUTHORIZATION = "Authorization";

    private String uri = "http://localhost:8080";
    private Map<String, String> headers = new HashMap<>();
    private Optional<String> prefix = Optional.empty();
    private Optional<Function<String, HawkularHttpClient>> httpClientProvider = Optional.empty();
    private final Map<String, String> globalTags = new HashMap<>();
    private final Map<String, Map<String, String>> perMetricTags = new HashMap<>();
    private final Collection<RegexTags> regexTags = new ArrayList<>();
    private Optional<Long> failoverCacheDuration = Optional.of(1000L * 60L * 10L); // In milliseconds; default: 10min
    private Optional<Integer> failoverCacheMaxSize = Optional.empty();

    /**
     * Create a new builder for {@link HawkularClient}
     * @param tenant the Hawkular tenant ID
     */
    public HawkularClientBuilder(String tenant) {
        headers.put(KEY_HEADER_TENANT, tenant);
    }

    /**
     * This is a shortcut function to use with automatically populated pojos such as coming from yaml config
     */
    public static HawkularClientBuilder fromConfig(HawkularClientConfig config) {
        HawkularClientBuilder builder = new HawkularClientBuilder(config.getTenant());
        if (config.getUri() != null) {
            builder.uri(config.getUri());
        }
        if (config.getPrefix() != null) {
            builder.prefixedWith(config.getPrefix());
        }
        if (config.getBearerToken() != null) {
            builder.bearerToken(config.getBearerToken());
        }
        if (config.getHeaders() != null) {
            config.getHeaders().forEach(builder::addHeader);
        }
        if (config.getGlobalTags() != null) {
            builder.globalTags(config.getGlobalTags());
        }
        if (config.getPerMetricTags() != null) {
            builder.perMetricTags(config.getPerMetricTags());
        }
        if (config.getUsername() != null && config.getPassword() != null) {
            builder.basicAuth(config.getUsername(), config.getPassword());
        }
        builder.failoverCacheDuration = Optional.ofNullable(config.getFailoverCacheDuration());
        builder.failoverCacheMaxSize = Optional.ofNullable(config.getFailoverCacheMaxSize());
        return builder;
    }

    /**
     * Set the URI for the Hawkular connection. Default URI is http://localhost:8080
     * @param uri base uri - do not include Hawkular Metrics path (/hawkular/metrics)
     */
    public HawkularClientBuilder uri(String uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Set username and password for basic HTTP authentication
     * @param username basic auth. username
     * @param password basic auth. password
     */
    public HawkularClientBuilder basicAuth(String username, String password) {
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        headers.put(KEY_HEADER_AUTHORIZATION, "Basic " + encoded);
        return this;
    }

    /**
     * Set the bearer token for the Authorization header in Hawkular HTTP connections. Can be used, for instance, for
     * OpenShift connections
     * @param token the bearer token
     */
    public HawkularClientBuilder bearerToken(String token) {
        headers.put(KEY_HEADER_AUTHORIZATION, "Bearer " + token);
        return this;
    }

    /**
     * Add a custom header to Hawkular HTTP connections
     * @param key header name
     * @param value header value
     */
    public HawkularClientBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * Configure a prefix for each metric name. Optional, but useful to identify single hosts
     */
    public HawkularClientBuilder prefixedWith(String prefix) {
        this.prefix = Optional.of(prefix);
        return this;
    }

    /**
     * Set all global tags at once. All metrics generated by this reporter instance will be tagged as such.
     * It overrides any global tag that was already set.
     * @param tags global tags
     */
    public HawkularClientBuilder globalTags(Map<String, String> tags) {
        this.globalTags.clear();
        this.globalTags.putAll(tags);
        return this;
    }

    /**
     * Set a global tag. All metrics generated by this reporter instance will be tagged as such.
     * @param key tag key
     * @param value tag value
     */
    public HawkularClientBuilder addGlobalTag(String key, String value) {
        this.globalTags.put(key, value);
        return this;
    }

    /**
     * Set all per-metric tags at once. It overrides any per-metric tag that was already set.
     * @param tags per-metric tags
     */
    public HawkularClientBuilder perMetricTags(Map<String, Map<String, String>> tags) {
        this.perMetricTags.clear();
        this.regexTags.clear();
        tags.forEach((k,v) -> {
            Optional<RegexTags> optRegexTags = RegexTags.checkAndCreate(k, v);
            if (optRegexTags.isPresent()) {
                this.regexTags.add(optRegexTags.get());
            } else {
                this.perMetricTags.put(k, v);
            }
        });
        return this;
    }

    /**
     * Set a tag on a given metric name
     * @param metric the metric name
     * @param key tag key
     * @param value tag value
     */
    public HawkularClientBuilder addMetricTag(String metric, String key, String value) {
        Optional<RegexTags> optRegexTags = RegexTags.checkAndCreate(metric, Collections.singletonMap(key, value));
        if (optRegexTags.isPresent()) {
            regexTags.add(optRegexTags.get());
        } else {
            final Map<String, String> tags;
            if (perMetricTags.containsKey(metric)) {
                tags = perMetricTags.get(metric);
            } else {
                tags = new HashMap<>();
                perMetricTags.put(metric, tags);
            }
            tags.put(key, value);
        }
        return this;
    }

    /**
     * Set a tag on a given metric name
     * @param pattern the regex pattern
     * @param key tag key
     * @param value tag value
     */
    public HawkularClientBuilder addRegexTag(Pattern pattern, String key, String value) {
        regexTags.add(new RegexTags(pattern, Collections.singletonMap(key, value)));
        return this;
    }

    /**
     * Set the failover cache duration (in milliseconds)<br/>
     * This cache is used to store post attempts in memory when the hawkular server cannot be reached<br/>
     * Default duration is 10 minutes
     * @param milliseconds number of milliseconds before eviction
     */
    public HawkularClientBuilder failoverCacheDuration(long milliseconds) {
        failoverCacheDuration = Optional.of(milliseconds);
        return this;
    }

    /**
     * Set the failover cache duration, in minutes<br/>
     * This cache is used to store post attempts in memory when the hawkular server cannot be reached<br/>
     * Default duration is 10 minutes
     * @param minutes number of minutes before eviction
     */
    public HawkularClientBuilder failoverCacheDurationInMinutes(long minutes) {
        failoverCacheDuration = Optional.of(TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES));
        return this;
    }

    /**
     * Set the failover cache duration, in hours<br/>
     * This cache is used to store post attempts in memory when the hawkular server cannot be reached<br/>
     * Default duration is 10 minutes
     * @param hours number of hours before eviction
     */
    public HawkularClientBuilder failoverCacheDurationInHours(long hours) {
        failoverCacheDuration = Optional.of(TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS));
        return this;
    }

    /**
     * Set the failover cache maximum size, in number of requests<br/>
     * This cache is used to store post attempts in memory when the hawkular server cannot be reached<br/>
     * By default this parameter is unset, which means there's no maximum
     * @param reqs max number of requests to store
     */
    public HawkularClientBuilder failoverCacheMaxSize(int reqs) {
        failoverCacheMaxSize = Optional.of(reqs);
        return this;
    }

    /**
     * Use a custom {@link HawkularHttpClient}
     * @param httpClientProvider function that provides a custom {@link HawkularHttpClient} from input URI as String
     */
    public HawkularClientBuilder useHttpClient(Function<String, HawkularHttpClient> httpClientProvider) {
        this.httpClientProvider = Optional.of(httpClientProvider);
        return this;
    }

    /**
     * Build the {@link HawkularClient}
     */
    public HawkularClient build() {
        HawkularHttpClient client = httpClientProvider
                .map(provider -> provider.apply(uri))
                .orElseGet(() -> new JdkHawkularHttpClient(uri));
        client.addHeaders(headers);
        client.setFailoverOptions(failoverCacheDuration, failoverCacheMaxSize);
        return new HawkularClient(new HawkularClientInfo(client, headers.get(KEY_HEADER_TENANT), prefix, globalTags, perMetricTags, regexTags));
    }

    public HawkularLogger buildLogger() {
        return new HawkularLogger(build());
    }
}