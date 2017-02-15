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
import java.util.OptionalInt;

import org.hawkular.metrics.client.config.HawkularClientInfo;

/**
 * @author Joel Takvorian
 */
public class GrafanaDatasource {
    private final String name;
    private final String tenant;
    private String url = "http://127.0.0.1:8080/hawkular/metrics";
    private boolean isProxy;
    private Optional<Credential> basicAuthCredential = Optional.empty();
    private Optional<String> bearerToken = Optional.empty();
    private OptionalInt id = OptionalInt.empty();

    public GrafanaDatasource(String name, String tenant) {
        this.name = name;
        this.tenant = tenant;
    }

    public static GrafanaDatasource fromHawkularConfig(String name, HawkularClientInfo info) {
        GrafanaDatasource ds = new GrafanaDatasource(name, info.getTenant())
                .url(info.getUri() + "/hawkular/metrics");
        info.getBasicAuthCredential().ifPresent(cred -> ds.basicAuth(cred.getUsername(), cred.getPassword()));
        info.getBearerToken().ifPresent(ds::token);
        return ds;
    }

    public GrafanaDatasource url(String url) {
        this.url = url;
        return this;
    }

    public GrafanaDatasource proxyMode() {
        this.isProxy = true;
        return this;
    }

    public GrafanaDatasource basicAuth(String username, String password) {
        this.basicAuthCredential = Optional.of(new Credential(username, password));
        this.bearerToken = Optional.empty();
        return this;
    }

    public GrafanaDatasource token(String token) {
        this.basicAuthCredential = Optional.empty();
        this.bearerToken = Optional.of(token);
        return this;
    }

    public GrafanaDatasource id(int id) {
        this.id = OptionalInt.of(id);
        return this;
    }

    public String getName() {
        return name;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{")
                .append("\"name\": \"").append(name).append("\"");
        id.ifPresent(i -> sb.append(", \"id\": ").append(i));
        sb.append(", \"type\": \"hawkular-datasource\"")
                .append(", \"url\": \"").append(url).append("\"")
                .append(", \"access\": ").append(isProxy ? "\"proxy\"" : "\"direct\"");
        basicAuthCredential.ifPresent(cred ->
            sb.append(", \"basicAuth\": true")
                    .append(", \"basicAuthUser\": \"").append(cred.username).append("\"")
                    .append(", \"basicAuthPassword\": \"").append(cred.password).append("\""));
        sb.append(", \"jsonData\": { ")
                .append("\"tenant\": \"").append(tenant).append("\"");
        bearerToken.ifPresent(token -> sb.append(", \"token\": \"").append(token).append("\""));
        return sb.append("}}").toString();
    }

    private static class Credential {
        private final String username;
        private final String password;

        private Credential(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
