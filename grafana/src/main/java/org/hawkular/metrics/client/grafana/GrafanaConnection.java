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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.OptionalInt;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * @author Joel Takvorian
 */
public class GrafanaConnection {
    private final String apiToken;
    private String uri = "http://localhost:3000";

    public GrafanaConnection(String apiToken) {
        this.apiToken = apiToken;
    }

    public GrafanaConnection uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void createOrUpdateDatasource(GrafanaDatasource datasource) throws IOException {
        OptionalInt optId = getDatasourceId(datasource.getName());
        if (optId.isPresent()) {
            datasource.id(optId.getAsInt());
            URL url = new URL(uri + "/api/datasources/" + optId.getAsInt());
            byte[] content = datasource.toJson().getBytes();
            put(url, content);
        } else {
            URL url = new URL(uri + "/api/datasources");
            byte[] content = datasource.toJson().getBytes();
            post(url, content);
        }
    }

    public OptionalInt getDatasourceId(String name) throws IOException {
        try {
            String json = get(new URL(uri + "/api/datasources/id/" + name));
            JsonReader jsonReader = Json.createReader(new StringReader(json));
            JsonObject jsonObject = jsonReader.readObject();
            if (jsonObject.containsKey("id")) {
                return OptionalInt.of(jsonObject.getInt("id"));
            }
        } catch (ResponseErrorException e) {
            if (e.getResponseCode() != 404) {
                throw e;
            }
        }
        return OptionalInt.empty();
    }

    public void sendDashboard(GrafanaDashboardMessage message) throws IOException {
        URL url = new URL(uri + "/api/dashboards/db");
        byte[] content = message.toJson().getBytes();
        post(url, content);
    }

    private String get(URL url) throws IOException {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        byte[] response = null;
        int responseCode = -1;
        try {
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiToken);
            responseCode = connection.getResponseCode();
            is = connection.getInputStream();
            final byte[] buffer = new byte[2 * 1024];
            baos = new ByteArrayOutputStream();
            int n;
            while ((n = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, n);
            }
            response = baos.toByteArray();
        } catch (IOException e) {
            if (responseCode > 0) {
                throw new ResponseErrorException(responseCode, e);
            }
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
            if (baos != null) {
                baos.close();
            }
        }
        return new String(response);
    }

    private void post(URL url, byte[] content) throws IOException {
        send(url, content, "POST");
    }

    private void put(URL url, byte[] content) throws IOException {
        send(url, content, "PUT");
    }

    private void send(URL url, byte[] content, String verb) throws IOException {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        int responseCode = -1;
        try {
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(verb);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", String.valueOf(content.length));
            connection.setRequestProperty("Authorization", "Bearer " + apiToken);
            OutputStream os = connection.getOutputStream();
            os.write(content);
            os.close();
            responseCode = connection.getResponseCode();
            is = connection.getInputStream();
            final byte[] buffer = new byte[2 * 1024];
            baos = new ByteArrayOutputStream();
            int n;
            while ((n = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, n);
            }
        } catch (IOException e) {
            if (responseCode > 0) {
                throw new ResponseErrorException(responseCode, e);
            }
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
            if (baos != null) {
                baos.close();
            }
        }
    }

    public static class ResponseErrorException extends IOException {
        private final int responseCode;

        public ResponseErrorException(int responseCode, IOException cause) {
            super(cause.getMessage(), cause);
            this.responseCode = responseCode;
        }

        public int getResponseCode() {
            return responseCode;
        }
    }
}
