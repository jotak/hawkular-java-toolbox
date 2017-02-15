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

import java.util.Optional;

/**
 * @author Joel Takvorian
 */
public class Tag {
    private final String key;
    private final Optional<String> value;

    private Tag(String key, Optional<String> value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Optional<String> getValue() {
        return value;
    }

    public String toQL() {
        return value.map(v -> key + "='" + v + "'").orElse(key);
    }

    @Override public String toString() {
        return "Tag{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

    public Tag valued(String value) {
        return new Tag(key, Optional.of(value));
    }

    public static Tag key(String key) {
        return new Tag(key, Optional.empty());
    }

    public static Tag keyValue(String key, String value) {
        return new Tag(key, Optional.of(value));
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (key != null ? !key.equals(tag.key) : tag.key != null) return false;
        return value != null ? value.equals(tag.value) : tag.value == null;
    }

    @Override public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
