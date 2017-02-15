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

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Joel Takvorian
 */
public class Tags {
    private final Map<String, Tag> tags = new LinkedHashMap<>();

    public static Tags singleton(String key, String value) {
        Tags tags = new Tags();
        tags.tags.put(key, Tag.keyValue(key, value));
        return tags;
    }

    public static Tags empty() {
        return new Tags();
    }

    public static Tags from(Tag... tags) {
        Tags all = new Tags();
        for (Tag tag : tags) {
            all.add(tag);
        }
        return all;
    }

    public static Tags from(Tags... others) {
        Tags tags = new Tags();
        for (Tags other : others) {
            tags.add(other);
        }
        return tags;
    }

    public static Tags fromMap(Map<String, String> map) {
        Tags tags = new Tags();
        map.forEach((k,v) -> tags.add(Tag.keyValue(k, v)));
        return tags;
    }

    public Map<String, Tag> getMap() {
        return tags;
    }

    public Map<String, String> toPresentMap() {
        return tags.entrySet().stream()
                .filter(e -> e.getValue().getValue().isPresent())
                .collect(toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().getValue().get()));
    }

    public Collection<Tag> asList() {
        return tags.values();
    }

    public void forEach(Consumer<Tag> consumer) {
        tags.values().forEach(consumer);
    }

    public void forEachPresent(BiConsumer<String, String> consumer) {
        forEach(tag -> tag.getValue().ifPresent(val -> consumer.accept(tag.getKey(), val)));
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    public void add(Tag tag) {
        tags.put(tag.getKey(), tag);
    }

    public void clear() {
        tags.clear();
    }

    public void add(Tags tags) {
        this.tags.putAll(tags.getMap());
    }

    public String toQL() {
        return tags.values().stream()
                .map(Tag::toQL)
                .collect(Collectors.joining(" AND "));
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tags tags1 = (Tags) o;

        return tags.equals(tags1.tags);
    }

    @Override public int hashCode() {
        return tags.hashCode();
    }

    @Override public String toString() {
        return "Tags{" +
                "tags=" + tags +
                '}';
    }
}
