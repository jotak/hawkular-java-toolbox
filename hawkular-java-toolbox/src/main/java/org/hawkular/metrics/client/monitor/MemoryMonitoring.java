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
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hawkular.metrics.client.HawkularClient;
import org.hawkular.metrics.client.model.Gauge;
import org.hawkular.metrics.client.model.Tag;
import org.hawkular.metrics.client.model.Tags;

import com.sun.management.OperatingSystemMXBean;

/**
 * @author Joel Takvorian
 */
public class MemoryMonitoring implements MonitoringSession.FeederSet {

    private final Tags tags;

    private MemoryMonitoring(Tags tags) {
        this.tags = tags;
    }

    @Override
    public Collection<MonitoringSession.Feeder> feeds(HawkularClient sessionBox) {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        List<MonitoringSession.Feeder> feeds = new ArrayList<>();
        Gauge sysFree = sessionBox.gauge("monitor.memory.system.free", tags);
        feeds.add(() -> sysFree.set(operatingSystemMXBean.getFreePhysicalMemorySize()));
        Gauge sysSwapFree = sessionBox.gauge("monitor.memory.system.swap.free", tags);
        feeds.add(() -> sysSwapFree.set(operatingSystemMXBean.getFreeSwapSpaceSize()));
        Gauge procHeap = sessionBox.gauge("monitor.memory.process.heap", tags);
        feeds.add(() -> procHeap.set(memoryUsage.getUsed()));
        return feeds;
    }

    public static MemoryMonitoring create() {
        return new MemoryMonitoring(Tags.empty());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Tags tags = Tags.empty();

        private Builder() {
        }

        public Builder withTags(Tags tags) {
            this.tags.add(tags);
            return this;
        }

        public Builder withTag(String key, String value) {
            this.tags.add(Tag.keyValue(key, value));
            return this;
        }

        public MemoryMonitoring build() {
            return new MemoryMonitoring(tags);
        }
    }
}
