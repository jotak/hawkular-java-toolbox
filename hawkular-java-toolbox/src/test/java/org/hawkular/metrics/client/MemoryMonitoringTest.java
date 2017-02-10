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

import java.util.concurrent.TimeUnit;

import org.hawkular.metrics.client.monitor.MemoryMonitoring;
import org.hawkular.metrics.client.monitor.MonitoringSession;
import org.junit.Test;

/**
 * @author Joel Takvorian
 */
public class MemoryMonitoringTest {

    private final HttpClientMock client = new HttpClientMock();

    @Test
    public void shouldMonitorMemory() throws InterruptedException {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        MonitoringSession session = hwk.prepareMonitoringSession(60, TimeUnit.MILLISECONDS)
            .feeds(MemoryMonitoring.create())
            .start();

        Thread.sleep(500);
        session.stop();

        int nbMetrics = 3;
        assertThat(client.getMetricsRestCalls().size()).isBetween(2*nbMetrics, 10*nbMetrics);
        assertThat(client.getTagsRestCalls()).isEmpty();
    }

    @Test
    public void shouldMonitorMemoryWithBuilder() throws InterruptedException {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        MonitoringSession session = hwk.prepareMonitoringSession(60, TimeUnit.MILLISECONDS)
                .feeds(MemoryMonitoring.builder()
                    .withTag("tag", "value")
                    .build())
                .start();

        session.stop();

        assertThat(client.getTagsRestCalls()).containsExactly(
                new HttpClientMock.TagsData(
                        "/gauges/monitor.memory.system.free/tags",
                        "{\"tag\":\"value\"}"),
                new HttpClientMock.TagsData(
                        "/gauges/monitor.memory.system.swap.free/tags",
                        "{\"tag\":\"value\"}"),
                new HttpClientMock.TagsData(
                        "/gauges/monitor.memory.process.heap/tags",
                        "{\"tag\":\"value\"}"));
    }
}
