package org.hawkular.metrics.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.hawkular.metrics.client.monitor.CPUMonitoring;
import org.hawkular.metrics.client.monitor.MonitoringSession;
import org.junit.Test;

/**
 * @author Joel Takvorian
 */
public class CPUMonitoringTest {

    private final HttpClientMock client = new HttpClientMock();

    @Test
    public void shouldMonitorCPU() throws InterruptedException {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        MonitoringSession session = hwk.prepareMonitoringSession(60, TimeUnit.MILLISECONDS)
            .feeds(CPUMonitoring.create())
            .start();

        Thread.sleep(500);
        session.stop();

        assertThat(client.getMetricsRestCalls().size()).isBetween(2, 10);
        assertThat(client.getTagsRestCalls()).isEmpty();
    }

    @Test
    public void shouldMonitorCPUWithBuilder() throws InterruptedException {
        HawkularClient hwk = HawkularFactory.load().builder()
                .useHttpClient(uri -> client)
                .build();
        MonitoringSession session = hwk.prepareMonitoringSession(60, TimeUnit.MILLISECONDS)
                .feeds(CPUMonitoring.builder()
                    .divideByNbCores()
                    .withTag("tag", "value")
                    .build())
                .start();

        session.stop();

        assertThat(client.getTagsRestCalls()).containsExactly(
                new HttpClientMock.TagsData(
                        "/gauges/monitor.cpu.core/tags",
                        "{\"tag\":\"value\"}"));
    }
}
