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
public class Timeline extends Metric {
    private final MetricChangeListener listener;

    public Timeline(String name, MetricChangeListener listener) {
        super("strings", name);
        this.listener = listener;
    }

    public void set(String value) {
        listener.onChanged(this, DataPoint.stringDataPoint(System.currentTimeMillis(), value));
    }

    public void set(String value, Map<String, String> tags) {
        listener.onChanged(this, DataPoint.stringDataPoint(System.currentTimeMillis(), value, tags));
    }
}
