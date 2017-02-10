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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

/**
 * @author Joel Takvorian
 */
public class HawkularLoggerTest {

    private final HttpClientMock client = new HttpClientMock();

    @Test
    public void shouldLogError() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        logger.error("I'm sorry, Dave. I'm afraid I can't do that.");

        assertSingleLog("2001.hal.error", "I'm sorry, Dave. I'm afraid I can't do that.");
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                    "/counters/2001.hal.error.count/tags",
                    "{\"source\":\"2001.hal\",\"severity\":\"error\"}"),
                new HttpClientMock.TagsData(
                    "/strings/2001.hal.error.logs/tags",
                    "{\"source\":\"2001.hal\",\"severity\":\"error\"}"));
    }

    @Test
    public void shouldLogErrorOnException() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        logger.error(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."));

        Map<String, String> expectedTags = new HashMap<>();
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.error", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.error.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"error\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.error.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"error\"}"));
    }

    @Test
    public void shouldLogErrorWithDPTags() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.error("I'm sorry, Dave. I'm afraid I can't do that.", tags);

        assertSingleLog("2001.hal.error", "I'm sorry, Dave. I'm afraid I can't do that.", tags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                    "/counters/2001.hal.error.count/tags",
                    "{\"source\":\"2001.hal\",\"severity\":\"error\"}"),
                new HttpClientMock.TagsData(
                    "/strings/2001.hal.error.logs/tags",
                    "{\"source\":\"2001.hal\",\"severity\":\"error\"}"));
    }

    @Test
    public void shouldLogErrorOnExceptionWithDPTags() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.error(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."), tags);

        Map<String, String> expectedTags = new HashMap<>(tags);
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.error", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.error.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"error\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.error.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"error\"}"));
    }

    @Test
    public void shouldLogWarning() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        logger.warn("I'm sorry, Dave. I'm afraid I can't do that.");

        assertSingleLog("2001.hal.warning", "I'm sorry, Dave. I'm afraid I can't do that.");
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.warning.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.warning.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"));
    }

    @Test
    public void shouldLogWarningOnException() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        logger.warn(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."));

        Map<String, String> expectedTags = new HashMap<>();
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.warning", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.warning.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.warning.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"));
    }

    @Test
    public void shouldLogWarningWithDPTags() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.warn("I'm sorry, Dave. I'm afraid I can't do that.", tags);

        assertSingleLog("2001.hal.warning", "I'm sorry, Dave. I'm afraid I can't do that.", tags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.warning.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.warning.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"));
    }

    @Test
    public void shouldLogWarningOnExceptionWithDPTags() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.warn(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."), tags);

        Map<String, String> expectedTags = new HashMap<>(tags);
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.warning", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.warning.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.warning.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"));
    }

    @Test
    public void shouldLogInfo() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        logger.info("I'm sorry, Dave. I'm afraid I can't do that.");

        assertSingleLog("2001.hal.info", "I'm sorry, Dave. I'm afraid I can't do that.");
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.info.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"info\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.info.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"info\"}"));
    }

    @Test
    public void shouldLogInfoOnException() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        logger.info(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."));

        Map<String, String> expectedTags = new HashMap<>();
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.info", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.info.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"info\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.info.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"info\"}"));
    }

    @Test
    public void shouldLogInfoWithDPTags() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.info("I'm sorry, Dave. I'm afraid I can't do that.", tags);

        assertSingleLog("2001.hal.info", "I'm sorry, Dave. I'm afraid I can't do that.", tags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.info.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"info\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.info.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"info\"}"));
    }

    @Test
    public void shouldLogInfoOnExceptionWithDPTags() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.info(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."), tags);

        Map<String, String> expectedTags = new HashMap<>(tags);
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.info", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.info.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"info\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.info.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"info\"}"));
    }

    @Test
    public void shouldLogDebug() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        logger.debug("I'm sorry, Dave. I'm afraid I can't do that.");

        assertSingleLog("2001.hal.debug", "I'm sorry, Dave. I'm afraid I can't do that.");
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.debug.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"debug\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.debug.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"debug\"}"));
    }

    @Test
    public void shouldLogDebugOnException() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        logger.debug(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."));

        Map<String, String> expectedTags = new HashMap<>();
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.debug", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.debug.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"debug\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.debug.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"debug\"}"));
    }

    @Test
    public void shouldLogDebugWithDPTags() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.debug("I'm sorry, Dave. I'm afraid I can't do that.", tags);

        assertSingleLog("2001.hal.debug", "I'm sorry, Dave. I'm afraid I can't do that.", tags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.debug.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"debug\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.debug.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"debug\"}"));
    }

    @Test
    public void shouldLogDebugOnExceptionWithDPTags() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.debug(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."), tags);

        Map<String, String> expectedTags = new HashMap<>(tags);
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.debug", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.debug.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"debug\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.debug.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"debug\"}"));
    }

    @Test
    public void shouldLogSeveralEntries() {
        HawkularLogger logger = HawkularFactory.load()
                .builder()
                .useHttpClient(uri -> client)
                .buildLogger("2001.hal");
        Map<String, String> tags = new HashMap<>();
        tags.put("t1", "v1");
        tags.put("t2", "v2");
        logger.error(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."), tags);

        Map<String, String> expectedTags = new HashMap<>(tags);
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleLog("2001.hal.error", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.error.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"error\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.error.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"error\"}"));

        client.clear();

        logger.error(new IllegalStateException("I'm sorry, Dave. I'm afraid I can't do that."));
        logger.warn("Dave, this conversation can serve no purpose anymore. Goodbye.");
        logger.warn("Just what do you think you're doing, Dave?");

        assertThat(client.getMetricsRestCalls()).hasSize(6);
        expectedTags.clear();
        expectedTags.put("class", "java.lang.IllegalStateException");
        assertSingleCounter(new JSONObject(client.getMetricsRestCalls().get(0)), "2001.hal.error", 2, expectedTags);
        assertSingleString(new JSONObject(client.getMetricsRestCalls().get(1)), "2001.hal.error", "java.lang.IllegalStateException: I'm sorry, Dave. I'm afraid I can't do that.", expectedTags);
        assertSingleCounter(new JSONObject(client.getMetricsRestCalls().get(2)), "2001.hal.warning", 1, Collections.emptyMap());
        assertSingleString(new JSONObject(client.getMetricsRestCalls().get(3)), "2001.hal.warning", "Dave, this conversation can serve no purpose anymore. Goodbye.", Collections.emptyMap());
        assertSingleCounter(new JSONObject(client.getMetricsRestCalls().get(4)), "2001.hal.warning", 2, Collections.emptyMap());
        assertSingleString(new JSONObject(client.getMetricsRestCalls().get(5)), "2001.hal.warning", "Just what do you think you're doing, Dave?", Collections.emptyMap());
        assertThat(client.getTagsRestCalls()).containsOnly(
                new HttpClientMock.TagsData(
                        "/counters/2001.hal.warning.count/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"),
                new HttpClientMock.TagsData(
                        "/strings/2001.hal.warning.logs/tags",
                        "{\"source\":\"2001.hal\",\"severity\":\"warning\"}"));
    }

    private void assertSingleLog(String metricNameBase, String sentence) {
        assertSingleLog(metricNameBase, sentence, Collections.emptyMap());
    }

    private void assertSingleLog(String metricNameBase, String sentence, Map<String, String> tags) {
        assertThat(client.getMetricsRestCalls()).hasSize(2);
        assertSingleCounter(new JSONObject(client.getMetricsRestCalls().get(0)), metricNameBase, 1, tags);
        assertSingleString(new JSONObject(client.getMetricsRestCalls().get(1)), metricNameBase, sentence, tags);
    }

    private void assertSingleCounter(JSONObject metrics, String metricNameBase, int value, Map<String, String> tags) {
        assertThat(metrics.keySet()).containsOnly("counters");
        JSONArray countersJson = metrics.getJSONArray("counters");
        assertThat(countersJson).hasSize(1);
        JSONObject counterJson = countersJson.getJSONObject(0);
        assertThat(counterJson.getString("id")).isEqualTo(metricNameBase + ".count");
        JSONObject datapoint = counterJson.getJSONArray("dataPoints").getJSONObject(0);
        assertThat(datapoint.getInt("value")).isEqualTo(value);
        if (tags.isEmpty()) {
            assertThat(datapoint.has("tags")).isFalse();
        } else {
            assertThat(datapoint.getJSONObject("tags").length()).isEqualTo(tags.size());
            tags.forEach((k, v) -> assertThat(datapoint.getJSONObject("tags").getString(k)).isEqualTo(v));
        }
    }

    private void assertSingleString(JSONObject metrics, String metricNameBase, String sentence, Map<String, String> tags) {
        assertThat(metrics.keySet()).containsOnly("strings");
        JSONArray stringsJson = metrics.getJSONArray("strings");
        assertThat(stringsJson).hasSize(1);
        JSONObject stringJson = stringsJson.getJSONObject(0);
        assertThat(stringJson.getString("id")).isEqualTo(metricNameBase + ".logs");
        JSONObject datapoint = stringJson.getJSONArray("dataPoints").getJSONObject(0);
        assertThat(datapoint.getString("value")).isEqualTo(sentence);
        if (tags.isEmpty()) {
            assertThat(datapoint.has("tags")).isFalse();
        } else {
            assertThat(datapoint.getJSONObject("tags").length()).isEqualTo(tags.size());
            tags.forEach((k, v) -> assertThat(datapoint.getJSONObject("tags").getString(k)).isEqualTo(v));
        }
    }
}
