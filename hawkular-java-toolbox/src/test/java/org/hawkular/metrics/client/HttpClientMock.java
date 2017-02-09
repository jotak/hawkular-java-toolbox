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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hawkular.metrics.client.common.http.HawkularHttpClient;
import org.hawkular.metrics.client.common.http.HawkularHttpResponse;

/**
 * @author Joel Takvorian
 */
class HttpClientMock implements HawkularHttpClient {
    private List<String> metricsRestCalls = new ArrayList<>();
    private List<TagsData> tagsRestCalls = new ArrayList<>();

    @Override public void addHeaders(Map<String, String> headers) {
    }

    @Override public HawkularHttpResponse postMetrics(String jsonBody) {
        metricsRestCalls.add(jsonBody);
        return null;
    }

    @Override public HawkularHttpResponse putTags(String type, String metricName, String jsonBody) {
        tagsRestCalls.add(new TagsData("/" + type + "/" + metricName + "/tags", jsonBody));
        return null;
    }

    @Override
    public void setFailoverOptions(Optional<Long> failoverCacheDuration, Optional<Integer> failoverCacheMaxSize) {
    }

    @Override public void manageFailover() {
    }

    List<String> getMetricsRestCalls() {
        return metricsRestCalls;
    }

    List<TagsData> getTagsRestCalls() {
        return tagsRestCalls;
    }

    public void clear() {
        metricsRestCalls.clear();
        tagsRestCalls.clear();
    }

    static class TagsData {
        final String resource;
        final String body;

        TagsData(String resource, String body) {
            this.resource = resource;
            this.body = body;
        }

        @Override public String toString() {
            return "TagsData{" +
                    "resource='" + resource + '\'' +
                    ", body='" + body + '\'' +
                    '}';
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TagsData tagsData = (TagsData) o;

            if (resource != null ? !resource.equals(tagsData.resource) : tagsData.resource != null) return false;
            return body != null ? body.equals(tagsData.body) : tagsData.body == null;
        }

        @Override public int hashCode() {
            int result = resource != null ? resource.hashCode() : 0;
            result = 31 * result + (body != null ? body.hashCode() : 0);
            return result;
        }
    }
}
