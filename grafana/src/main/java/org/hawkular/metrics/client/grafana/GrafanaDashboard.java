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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hawkular.metrics.client.HawkularLogger;

/**
 * @author Joel Takvorian
 */
public class GrafanaDashboard {

    private String title;
    private List<GrafanaRow> rows = new ArrayList<>();
    private List<GrafanaAnnotation> annotations = new ArrayList<>();

    public GrafanaDashboard() {
    }

    public GrafanaDashboard title(String title) {
        this.title = title;
        return this;
    }

    public GrafanaDashboard addRow(GrafanaRow row) {
        this.rows.add(row);
        return this;
    }

    public GrafanaDashboard addAnnotation(GrafanaAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public GrafanaDashboard addLoggerAnnotations(HawkularLogger logger) {
        String baseName = logger.getMetricsBase();
        annotations.add(new GrafanaAnnotation("Error", baseName + "error.logs").color("rgba(255, 96, 96, 1)"));
        annotations.add(new GrafanaAnnotation("Warning", baseName + "warning.logs").color("rgba(255, 150, 96, 1)"));
        annotations.add(new GrafanaAnnotation("Info", baseName + "info.logs").color("rgba(96, 128, 255, 1)"));
        annotations.add(new GrafanaAnnotation("Debug", baseName + "debug.logs").color("rgba(128, 128, 128, 1)"));
        return this;
    }

    public String toJson(GrafanaDashboardContext context) {
        return "{" +
                "\"id\":null" +
                ", \"tags\":[]" +
                ", \"timezone\":\"browser\"" +
                ", \"schemaVersion\":6" +
                ", \"version\":0" +
                ", \"title\":\"" + title + "\"" +
                ", \"rows\":[" + rows.stream().map(row -> row.toJson(context))
                        .collect(Collectors.joining(",")) + "]" +
                ", \"annotations\": {\"list\": [" + annotations.stream().map(ann -> ann.toJson(context))
                        .collect(Collectors.joining(",")) + "]}" +
                "}";
    }
}
