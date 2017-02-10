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
import java.util.Map;
import java.util.function.Function;

import org.hawkular.metrics.client.model.AvailabilityMetric;
import org.hawkular.metrics.client.model.Counter;
import org.hawkular.metrics.client.model.Gauge;
import org.hawkular.metrics.client.model.Logger;
import org.hawkular.metrics.client.model.Watch;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

/**
 * @author Joel Takvorian
 */
public class HawkularClientTest {

    private final HttpClientMock client = new HttpClientMock();

    @Test
    public void shouldFeedLogger() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        Logger logger = hwk.logger("2001.hal.log");

        assertThat(logger.getName()).isEqualTo("2001.hal.log");
        assertThat(client.getMetricsRestCalls()).isEmpty();
        assertThat(client.getTagsRestCalls()).hasSize(0);

        logger.log("I'm sorry, Dave. I'm afraid I can't do that.");

        assertThat(client.getMetricsRestCalls()).hasSize(1);
        assertSingleValue(new JSONObject(client.getMetricsRestCalls().get(0)),
                "strings",
                "2001.hal.log",
                "I'm sorry, Dave. I'm afraid I can't do that.",
                json -> json.getString("value"),
                Collections.emptyMap());
        assertThat(client.getTagsRestCalls()).hasSize(0);
    }

    @Test
    public void shouldFeedGaugeWithTags() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        Gauge gauge = hwk.gauge("2001.hal.heat", Collections.singletonMap("t1", "v1"));
        assertThat(gauge.getName()).isEqualTo("2001.hal.heat");
        assertThat(client.getMetricsRestCalls()).isEmpty();
        assertThat(client.getTagsRestCalls()).hasSize(1);
        assertThat(client.getTagsRestCalls().get(0).resource).isEqualTo("/gauges/2001.hal.heat/tags");
        assertThat(client.getTagsRestCalls().get(0).body).isEqualTo("{\"t1\":\"v1\"}");
        client.clear();

        gauge.set(5.5);
        assertThat(client.getMetricsRestCalls()).hasSize(1);
        assertSingleValue(new JSONObject(client.getMetricsRestCalls().get(0)),
                "gauges",
                "2001.hal.heat",
                5.5,
                json -> json.getDouble("value"),
                Collections.emptyMap());
        assertThat(client.getTagsRestCalls()).hasSize(0);
    }

    @Test
    public void shouldFeedCounterWithDPTags() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        Counter counter = hwk.counter("2001.hal.quotes");
        assertThat(counter.getName()).isEqualTo("2001.hal.quotes");
        assertThat(client.getMetricsRestCalls()).isEmpty();
        assertThat(client.getTagsRestCalls()).isEmpty();

        counter.inc(Collections.singletonMap("t1", "v1"));
        assertThat(client.getMetricsRestCalls()).hasSize(1);
        assertSingleValue(new JSONObject(client.getMetricsRestCalls().get(0)),
                "counters",
                "2001.hal.quotes",
                1,
                json -> json.getInt("value"),
                Collections.singletonMap("t1", "v1"));
        assertThat(client.getTagsRestCalls()).hasSize(0);

        client.clear();
        counter.inc(Collections.singletonMap("t1", "v2"));
        assertThat(client.getMetricsRestCalls()).hasSize(1);
        assertSingleValue(new JSONObject(client.getMetricsRestCalls().get(0)),
                "counters",
                "2001.hal.quotes",
                2,
                json -> json.getInt("value"),
                Collections.singletonMap("t1", "v2"));
        assertThat(client.getTagsRestCalls()).hasSize(0);
    }

    @Test
    public void shouldFeedWatch() throws InterruptedException {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        Watch watch = hwk.watch("2001.hal.boottime");
        assertThat(watch.getName()).isEqualTo("2001.hal.boottime");
        assertThat(client.getMetricsRestCalls()).isEmpty();
        assertThat(client.getTagsRestCalls()).isEmpty();

        Thread.sleep(50);
        watch.tick();

        Double value = new JSONObject(client.getMetricsRestCalls().get(0))
                .getJSONArray("gauges")
                .getJSONObject(0)
                .getJSONArray("dataPoints")
                .getJSONObject(0)
                .getDouble("value");
        assertThat(value).isBetween(50d, 70d);
    }

    @Test
    public void shouldCreateAvailability() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        AvailabilityMetric avail = hwk.availability("2001.hal.health");
        assertThat(avail.getName()).isEqualTo("2001.hal.health");
        assertThat(client.getMetricsRestCalls()).isEmpty();
        assertThat(client.getTagsRestCalls()).isEmpty();

        avail.down();
        assertThat(client.getMetricsRestCalls()).hasSize(1);
        assertSingleValue(new JSONObject(client.getMetricsRestCalls().get(0)),
                "availability",
                "2001.hal.health",
                "DOWN",
                json -> json.getString("value"),
                Collections.emptyMap());
        assertThat(client.getTagsRestCalls()).hasSize(0);

        client.clear();
        avail.up();
        assertThat(client.getMetricsRestCalls()).hasSize(1);
        assertSingleValue(new JSONObject(client.getMetricsRestCalls().get(0)),
                "availability",
                "2001.hal.health",
                "UP",
                json -> json.getString("value"),
                Collections.emptyMap());
        assertThat(client.getTagsRestCalls()).hasSize(0);

        client.clear();
        avail.unknown();
        assertThat(client.getMetricsRestCalls()).hasSize(1);
        assertSingleValue(new JSONObject(client.getMetricsRestCalls().get(0)),
                "availability",
                "2001.hal.health",
                "UNKNOWN",
                json -> json.getString("value"),
                Collections.emptyMap());
        assertThat(client.getTagsRestCalls()).hasSize(0);
    }

    private static <T> void assertSingleValue(JSONObject metrics,
                                              String metricType,
                                              String id,
                                              T value,
                                              Function<JSONObject, T> f,
                                              Map<String, String> tags) {
        assertThat(metrics.keySet()).containsOnly(metricType);
        JSONArray jsonArray = metrics.getJSONArray(metricType);
        assertThat(jsonArray).hasSize(1);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        assertThat(jsonObject.getString("id")).isEqualTo(id);
        JSONObject datapoint = jsonObject.getJSONArray("dataPoints").getJSONObject(0);
        assertThat(f.apply(datapoint)).isEqualTo(value);
        if (tags.isEmpty()) {
            assertThat(datapoint.has("tags")).isFalse();
        } else {
            assertThat(datapoint.getJSONObject("tags").length()).isEqualTo(tags.size());
            tags.forEach((k, v) -> assertThat(datapoint.getJSONObject("tags").getString(k)).isEqualTo(v));
        }
    }
}
