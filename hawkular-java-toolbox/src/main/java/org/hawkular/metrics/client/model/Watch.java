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
package org.hawkular.metrics.client.model;

import java.util.Map;

/**
 * @author Joel Takvorian
 */
public class Watch extends Metric {

    private final MetricChangeListener listener;
    private long timestamp;

    public Watch(String name, MetricChangeListener listener) {
        super("gauges", name);
        this.listener = listener;
        reset();
    }

    public void reset() {
        timestamp = System.currentTimeMillis();
    }

    public void tick() {
        long tick = System.currentTimeMillis();
        listener.onChanged(this, DataPoint.doubleDataPoint(System.currentTimeMillis(), tick - timestamp));
        timestamp = tick;
    }

    public void tick(Map<String, String> tags) {
        long tick = System.currentTimeMillis();
        listener.onChanged(this, DataPoint.doubleDataPoint(System.currentTimeMillis(), tick - timestamp, tags));
        timestamp = tick;
    }
}
