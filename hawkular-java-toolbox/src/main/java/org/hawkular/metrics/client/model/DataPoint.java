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
import java.util.function.BiConsumer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Joel Takvorian
 */
public class DataPoint<T> {

    private static final BiConsumer<Double, JsonObjectBuilder> DOUBLE_JSON_ADDER = (data, builder) -> builder.add("value", data);
    private static final BiConsumer<Long, JsonObjectBuilder> LONG_JSON_ADDER = (data, builder) -> builder.add("value", data);
    private static final BiConsumer<String, JsonObjectBuilder> STRING_JSON_ADDER = (data, builder) -> builder.add("value", data);
    private static final BiConsumer<Availability, JsonObjectBuilder> AVAIL_JSON_ADDER = (data, builder) -> builder.add("value", data.toString());

    private final long timestamp;
    private final T data;
    private final Map<String, String> dpTags;
    private BiConsumer<T, JsonObjectBuilder> valueAdder;

    public DataPoint(long timestamp, T data, Map<String, String> dpTags, BiConsumer<T, JsonObjectBuilder> valueAdder) {
        this.timestamp = timestamp;
        this.data = data;
        this.dpTags = dpTags;
        this.valueAdder = valueAdder;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }

    public Map<String, String> getDpTags() {
        return dpTags;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("timestamp", timestamp);
        valueAdder.accept(data, builder);
        if (dpTags != null && !dpTags.isEmpty()) {
            JsonObjectBuilder tags = Json.createObjectBuilder();
            dpTags.forEach(tags::add);
            builder.add("tags", tags.build());
        }
        return builder.build();
    }

    public static DataPoint<Double> doubleDataPoint(long timestamp, double data, Map<String, String> dpTags) {
        return new DataPoint<>(timestamp, data, dpTags, DOUBLE_JSON_ADDER);
    }

    public static DataPoint<Long> longDataPoint(long timestamp, long data, Map<String, String> dpTags) {
        return new DataPoint<>(timestamp, data, dpTags, LONG_JSON_ADDER);
    }

    public static DataPoint<String> stringDataPoint(long timestamp, String data, Map<String, String> dpTags) {
        return new DataPoint<>(timestamp, data, dpTags, STRING_JSON_ADDER);
    }

    public static DataPoint<Availability> availDataPoint(long timestamp, Availability data, Map<String, String> dpTags) {
        return new DataPoint<>(timestamp, data, dpTags, AVAIL_JSON_ADDER);
    }

    public static DataPoint<Double> doubleDataPoint(long timestamp, double data) {
        return new DataPoint<>(timestamp, data, null, DOUBLE_JSON_ADDER);
    }

    public static DataPoint<Long> longDataPoint(long timestamp, long data) {
        return new DataPoint<>(timestamp, data, null, LONG_JSON_ADDER);
    }

    public static DataPoint<String> stringDataPoint(long timestamp, String data) {
        return new DataPoint<>(timestamp, data, null, STRING_JSON_ADDER);
    }

    public static DataPoint<Availability> availDataPoint(long timestamp, Availability data) {
        return new DataPoint<>(timestamp, data, null, AVAIL_JSON_ADDER);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataPoint<?> dataPoint = (DataPoint<?>) o;

        if (timestamp != dataPoint.timestamp) return false;
        if (data != null ? !data.equals(dataPoint.data) : dataPoint.data != null) return false;
        return dpTags != null ? dpTags.equals(dataPoint.dpTags) : dataPoint.dpTags == null;
    }

    @Override public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (dpTags != null ? dpTags.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "DataPoint{" +
                "timestamp=" + timestamp +
                ", data=" + data +
                ", dpTags=" + dpTags +
                '}';
    }
}
