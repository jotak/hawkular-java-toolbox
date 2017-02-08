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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HawkularLogger {

    private static final Map<String, String> MAP_SEVERITY_DEBUG = Collections.singletonMap("severity", "debug");
    private static final Map<String, String> MAP_SEVERITY_INFO = Collections.singletonMap("severity", "info");
    private static final Map<String, String> MAP_SEVERITY_WARNING = Collections.singletonMap("severity", "warning");
    private static final Map<String, String> MAP_SEVERITY_ERROR = Collections.singletonMap("severity", "error");

    private final HawkularClient inst;

    HawkularLogger(HawkularClient inst) {
        this.inst = inst;
    }

    public void debug(String message) {
        debug(message, null);
    }

    public void debug(String message, Map<String, String> dpTags) {
        inst.counter("debug.count", MAP_SEVERITY_DEBUG).inc();
        inst.timeline("debug.timeline", MAP_SEVERITY_DEBUG).set(message, dpTags);
    }

    public void debug(Throwable t) {
        debug(throwableToString(t), Collections.singletonMap("class", t.getClass().getName()));
    }

    public void debug(Throwable t, Map<String, String> dpTags) {
        Map<String, String> allDpTags = new HashMap<>(dpTags);
        allDpTags.put("class", t.getClass().getName());
        debug(throwableToString(t), allDpTags);
    }

    public void info(String message) {
        info(message, null);
    }

    public void info(String message, Map<String, String> dpTags) {
        inst.counter("info.count", MAP_SEVERITY_INFO).inc();
        inst.timeline("info.timeline", MAP_SEVERITY_INFO).set(message, dpTags);
    }

    public void info(Throwable t) {
        info(throwableToString(t), Collections.singletonMap("class", t.getClass().getName()));
    }

    public void info(Throwable t, Map<String, String> dpTags) {
        Map<String, String> allDpTags = new HashMap<>(dpTags);
        allDpTags.put("class", t.getClass().getName());
        info(throwableToString(t), allDpTags);
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void warn(String message, Map<String, String> dpTags) {
        inst.counter("warning.count", MAP_SEVERITY_WARNING).inc();
        inst.timeline("warning.timeline", MAP_SEVERITY_WARNING).set(message, dpTags);
    }

    public void warn(Throwable t) {
        warn(throwableToString(t), Collections.singletonMap("class", t.getClass().getName()));
    }

    public void warn(Throwable t, Map<String, String> dpTags) {
        Map<String, String> allDpTags = new HashMap<>(dpTags);
        allDpTags.put("class", t.getClass().getName());
        warn(throwableToString(t), allDpTags);
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(String message, Map<String, String> dpTags) {
        inst.counter("error.count", MAP_SEVERITY_ERROR).inc();
        inst.timeline("error.timeline", MAP_SEVERITY_ERROR).set(message, dpTags);
    }

    public void error(Throwable t) {
        error(throwableToString(t), Collections.singletonMap("class", t.getClass().getName()));
    }

    public void error(Throwable t, Map<String, String> dpTags) {
        Map<String, String> allDpTags = new HashMap<>(dpTags);
        allDpTags.put("class", t.getClass().getName());
        error(throwableToString(t), allDpTags);
    }

    private static String throwableToString(Throwable t) {
        StringBuilder sb = new StringBuilder(t.getClass().getName());
        if (t.getMessage() != null) {
            sb.append(t.getMessage());
        }
        return sb.toString();
    }
}
