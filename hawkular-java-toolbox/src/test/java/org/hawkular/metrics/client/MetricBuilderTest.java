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

import org.hawkular.metrics.client.model.AvailabilityMetric;
import org.hawkular.metrics.client.model.Counter;
import org.hawkular.metrics.client.model.Gauge;
import org.hawkular.metrics.client.model.Logger;
import org.hawkular.metrics.client.model.Watch;
import org.junit.Test;

/**
 * @author Joel Takvorian
 */
public class MetricBuilderTest {

    private final HttpClientMock client = new HttpClientMock();

    @Test
    public void shouldCreateLog() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        Logger log = hwk.metricBuilder()
                .addSegment("movie", "2001")
                .addSegment("character", "hal")
                .addSegment("metric", "logs")
                .addTag("type", "AI")
                .toLogger();

        assertThat(log.getName()).isEqualTo("2001.hal.logs");
        assertThat(client.getMetricsRestCalls()).isEmpty();
        assertThat(client.getTagsRestCalls()).hasSize(1);
        assertThat(client.getTagsRestCalls().get(0).resource).isEqualTo("/strings/2001.hal.logs/tags");
        assertThat(client.getTagsRestCalls().get(0).body).contains("\"character\":\"hal\"")
                .contains("\"movie\":\"2001\"")
                .contains("\"type\":\"AI\"")
                .contains("\"metric\":\"logs\"");
    }

    @Test
    public void shouldCreateGauge() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        Gauge gauge = hwk.metricBuilder()
                .addSegment("movie", "2001")
                .addSegment("character", "hal")
                .addSegment("metric", "heat")
                .toGauge();

        assertThat(gauge.getName()).isEqualTo("2001.hal.heat");
    }

    @Test
    public void shouldCreateCounterWithPrefix() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .prefixedWith("imdb.")
                .build();
        Counter counter = hwk.metricBuilder()
                .addSegment("movie", "2001")
                .addSegment("character", "hal")
                .addSegment("metric", "cycles")
                .toCounter();

        assertThat(counter.getName()).isEqualTo("imdb.2001.hal.cycles");
    }

    @Test
    public void shouldCreateWatchWithDashes() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        Watch watch = hwk.metricBuilder()
                .addSegment("movie", "2001")
                .addSegment("character", "hal")
                .addSegment("metric", "boottime")
                .separator("-")
                .toWatch();

        assertThat(watch.getName()).isEqualTo("2001-hal-boottime");
    }

    @Test
    public void shouldCreateAvailability() {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        AvailabilityMetric availabilityMetric = hwk.metricBuilder()
                .addSegment("movie", "2001")
                .addSegment("character", "hal")
                .addSegment("metric", "sane")
                .toAvailability();

        assertThat(availabilityMetric.getName()).isEqualTo("2001.hal.sane");
    }
}
