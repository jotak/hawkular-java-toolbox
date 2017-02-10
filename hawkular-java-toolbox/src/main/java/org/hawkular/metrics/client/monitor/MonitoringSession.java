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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hawkular.metrics.client.HawkularClient;

/**
 * @author Joel Takvorian
 */
public class MonitoringSession {

    private final ScheduledExecutorService executorService;

    public MonitoringSession(long frequency, TimeUnit timeUnit, int threadPoolSize, List<Feeder> feeds) {
        executorService = Executors.newScheduledThreadPool(threadPoolSize);
        feeds.forEach(feed -> executorService.scheduleAtFixedRate(feed::feed, 0, frequency, timeUnit));
    }

    public void stop() {
        executorService.shutdownNow();
    }

    public static class Builder {
        private final HawkularClient sessionBox;
        private final long frequency;
        private final TimeUnit timeUnit;
        private final List<Feeder> feeds = new ArrayList<>();
        private int threadPoolSize = 5;

        public Builder(HawkularClient sessionBox, long frequency, TimeUnit timeUnit) {
            this.sessionBox = sessionBox;
            this.frequency = frequency;
            this.timeUnit = timeUnit;
        }

        public Builder feed(Feeder feed) {
            feeds.add(feed);
            return this;
        }

        public Builder feeds(FeederSet feederSet) {
            this.feeds.addAll(feederSet.feeds(sessionBox));
            return this;
        }

        public Builder threadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public MonitoringSession start() {
            return new MonitoringSession(frequency, timeUnit, threadPoolSize, feeds);
        }
    }

    interface Feeder {
        void feed();
    }

    interface FeederSet {
        Collection<Feeder> feeds(HawkularClient sessionBox);
    }
}
