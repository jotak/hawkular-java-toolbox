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

import org.hawkular.metrics.client.common.http.HawkularHttpClient;
import org.hawkular.metrics.client.config.HawkularClientInfo;
import org.hawkular.metrics.client.model.DataPoint;
import org.hawkular.metrics.client.model.Metric;
import org.hawkular.metrics.client.model.MetricChangeListener;
import org.hawkular.metrics.client.model.Tags;

// TODO: allow batch changes
public class MetricsNotifier implements MetricChangeListener {

    private final HawkularHttpClient hawkularClient;

    MetricsNotifier(HawkularClientInfo config) {
        this.hawkularClient = config.getHttpClient();
    }

    @Override public void onChanged(Metric metric, DataPoint<?> dp) {
        hawkularClient.postMetrics(HawkularJson.metricToString(metric, dp));
    }

    @Override public void tag(Metric metric, Tags tags) {
        hawkularClient.putTags(metric.getHawkularType(), metric.getName(), HawkularJson.tagsToString(tags));
    }
}
