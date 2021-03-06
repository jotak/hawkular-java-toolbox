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

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Joel Takvorian
 */
public class Counter extends Metric {
    private LongAdder count = new LongAdder();

    public Counter(String name, MetricChangeListener listener) {
        super("counters", name, listener);
    }

    public void inc() {
        count.increment();
        listener.onChanged(this, DataPoint.longDataPoint(System.currentTimeMillis(), count.longValue()));
    }

    public void inc(Tags tags) {
        count.increment();
        listener.onChanged(this, DataPoint.longDataPoint(System.currentTimeMillis(), count.longValue(), tags));
    }

    public Long getCount() {
        return count.longValue();
    }
}
