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
public class AvailabilityMetric extends Metric {
    private final MetricChangeListener listener;

    public AvailabilityMetric(String name, MetricChangeListener listener) {
        super("availability", name);
        this.listener = listener;
    }

    public void up() {
        listener.onChanged(this, DataPoint.availDataPoint(System.currentTimeMillis(), Availability.UP));
    }

    public void down() {
        listener.onChanged(this, DataPoint.availDataPoint(System.currentTimeMillis(), Availability.DOWN));
    }

    public void unknown() {
        listener.onChanged(this, DataPoint.availDataPoint(System.currentTimeMillis(), Availability.UNKNOWN));
    }

    public void admin() {
        listener.onChanged(this, DataPoint.availDataPoint(System.currentTimeMillis(), Availability.ADMIN));
    }

    public void up(Map<String, String> tags) {
        listener.onChanged(this, DataPoint.availDataPoint(System.currentTimeMillis(), Availability.UP, tags));
    }

    public void down(Map<String, String> tags) {
        listener.onChanged(this, DataPoint.availDataPoint(System.currentTimeMillis(), Availability.DOWN, tags));
    }

    public void unknown(Map<String, String> tags) {
        listener.onChanged(this, DataPoint.availDataPoint(System.currentTimeMillis(), Availability.UNKNOWN, tags));
    }

    public void admin(Map<String, String> tags) {
        listener.onChanged(this, DataPoint.availDataPoint(System.currentTimeMillis(), Availability.ADMIN, tags));
    }
}
