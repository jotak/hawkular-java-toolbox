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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.hawkular.metrics.client.model.DataPoint;
import org.hawkular.metrics.client.model.Metric;
import org.hawkular.metrics.client.model.Tags;

/**
 * Some Json utility for Hawkular data model
 * @author Joel Takvorian
 */
public final class HawkularJson {

    private HawkularJson() {
    }

    public static String metricToString(Metric metric, DataPoint<?> dp) {
        return Json.createObjectBuilder()
                .add(metric.getHawkularType(), Json.createArrayBuilder()
                        .add(metricJson(metric.getName(), dp)))
                .build()
                .toString();
    }

    public static String tagsToString(Tags tags) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        tags.forEachPresent(jsonObjectBuilder::add);
        return jsonObjectBuilder.build().toString();
    }

    private static <T> JsonObject metricJson(String name, DataPoint<T> dataPoint) {
        return Json.createObjectBuilder()
                .add("id", name)
                .add("dataPoints", Json.createArrayBuilder().add(dataPoint.toJson()).build())
                .build();
    }
}
