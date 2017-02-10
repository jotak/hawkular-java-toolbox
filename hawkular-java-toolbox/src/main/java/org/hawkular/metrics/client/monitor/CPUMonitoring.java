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
package org.hawkular.metrics.client.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hawkular.metrics.client.HawkularClient;
import org.hawkular.metrics.client.model.Gauge;

import com.sun.management.OperatingSystemMXBean;

/**
 * @author Joel Takvorian
 */
public class CPUMonitoring implements MonitoringSession.FeederSet {

    private final boolean divideByNbCores;
    private final Map<String, String> tags;
    private Measures lastMeasures;

    private CPUMonitoring(boolean divideByNbCores, Map<String, String> tags) {
        this.divideByNbCores = divideByNbCores;
        this.tags = tags;
    }

    @Override
    public Collection<MonitoringSession.Feeder> feeds(HawkularClient sessionBox) {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<MonitoringSession.Feeder> feeds = new ArrayList<>();
        Gauge coreCpuGauge = sessionBox.gauge("monitor.cpu.core", tags);
        feeds.add(() -> reportCPU(operatingSystemMXBean, runtimeMXBean).ifPresent(coreCpuGauge::set));
        return feeds;
    }

    private Optional<Double> reportCPU(OperatingSystemMXBean operatingSystemMXBean, RuntimeMXBean runtimeMXBean) {
        int availableProcessors = divideByNbCores ? operatingSystemMXBean.getAvailableProcessors() : 1;
        long upTime = runtimeMXBean.getUptime();
        long processCpuTime = operatingSystemMXBean.getProcessCpuTime();

        if (lastMeasures == null
                || lastMeasures.availableProcessors != availableProcessors
                || lastMeasures.processCpuTime < 0) {
            lastMeasures = new Measures(availableProcessors, upTime, processCpuTime);
            return Optional.empty();
        }

        long elapsedCpu = processCpuTime - lastMeasures.processCpuTime;
        long elapsedTime = upTime - lastMeasures.upTime;
        lastMeasures = new Measures(availableProcessors, upTime, processCpuTime);

        return Optional.of(Math.min(99D, elapsedCpu / (elapsedTime * 10000D * availableProcessors)));
    }

    public static CPUMonitoring create() {
        return new CPUMonitoring(false, Collections.emptyMap());
    }

    public static Builder builder() {
        return new Builder();
    }

    private static class Measures {
        private final int availableProcessors;
        private final long upTime;
        private final long processCpuTime;

        private Measures(int availableProcessors, long upTime, long processCpuTime) {
            this.availableProcessors = availableProcessors;
            this.upTime = upTime;
            this.processCpuTime = processCpuTime;
        }
    }

    public static class Builder {
        private boolean divideByNbCores;
        private Map<String, String> tags = new HashMap<>();

        private Builder() {
        }

        public Builder divideByNbCores() {
            this.divideByNbCores = true;
            return this;
        }

        public Builder withTags(Map<String, String> tags) {
            this.tags.putAll(tags);
            return this;
        }

        public Builder withTag(String key, String value) {
            this.tags.put(key, value);
            return this;
        }

        public CPUMonitoring build() {
            return new CPUMonitoring(divideByNbCores, tags);
        }
    }
}
