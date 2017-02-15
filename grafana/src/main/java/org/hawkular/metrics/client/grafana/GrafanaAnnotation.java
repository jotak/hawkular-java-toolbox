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
package org.hawkular.metrics.client.grafana;

import java.util.Optional;

import org.hawkular.metrics.client.model.Logger;

/**
 * @author Joel Takvorian
 */
public class GrafanaAnnotation {

    private final String name;
    private final String query;
    private boolean enabled;
    private Optional<String> iconColor = Optional.empty();

    public GrafanaAnnotation(String name, String query) {
        this.name = name;
        this.query = query;
    }

    public static GrafanaAnnotation fromLogger(Logger logger, String displayName) {
        return new GrafanaAnnotation(displayName, logger.getName());
    }

    public static GrafanaAnnotation fromLogger(Logger logger) {
        return new GrafanaAnnotation(logger.getName(), logger.getName());
    }

    public GrafanaAnnotation color(String color) {
        this.iconColor = Optional.of(color);
        return this;
    }

    public GrafanaAnnotation enabled() {
        this.enabled = true;
        return this;
    }

    public String toJson(GrafanaDashboardContext context) {
        StringBuilder sb = new StringBuilder("{")
                .append("\"name\": \"").append(name).append("\"")
                .append(", \"datasource\": \"").append(context.getDatasource()).append("\"")
                .append(", \"query\": \"").append(query).append("\"")
                .append(", \"enabled\": ").append(enabled);
        iconColor.ifPresent(c -> sb.append(", \"iconColor\": \"").append(c).append("\""));
        return sb.append("}").toString();
    }
}
