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

/**
 * @author Joel Takvorian
 */
public class GrafanaPanel {
    private final String type;
    private String title = "New panel";
    private final List<GrafanaTarget> targets = new ArrayList<>();

    private GrafanaPanel(String type) {
        this.type = type;
    }

    public static GrafanaPanel graph() {
        return new GrafanaPanel("graph");
    }

    public static GrafanaPanel singlestat() {
        return new GrafanaPanel("singlestat");
    }

    public GrafanaPanel addTarget(GrafanaTarget target) {
        this.targets.add(target);
        return this;
    }

    public GrafanaPanel title(String title) {
        this.title = title;
        return this;
    }

    public String toJson(GrafanaDashboardContext context, int span) {
        return "{" +
                "\"type\": \"" + type + "\"" +
                ", \"datasource\": \"" + context.getDatasource() + "\"" +
                ", \"title\": \"" + title + "\"" +
                ", \"targets\":[" + targets.stream().map(GrafanaTarget::toJson)
                        .collect(Collectors.joining(",")) + "]" +
                ", \"id\": " + context.getNextPanelId() +
                ", \"span\": " + span +
                ", \"editable\": true" +
                ", \"renderer\": \"flot\"" +
                "}";
    }
}
