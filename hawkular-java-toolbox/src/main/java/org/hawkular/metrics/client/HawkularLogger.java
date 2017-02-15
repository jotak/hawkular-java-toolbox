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

import org.hawkular.metrics.client.model.Tag;
import org.hawkular.metrics.client.model.Tags;

public class HawkularLogger {

    private static final Tag TAG_SEVERITY = Tag.key("severity");
    private static final Tags TAG_SEVERITY_DEBUG = Tags.from(TAG_SEVERITY.valued("debug"));
    private static final Tags TAG_SEVERITY_INFO = Tags.from(TAG_SEVERITY.valued("info"));
    private static final Tags TAG_SEVERITY_WARNING = Tags.from(TAG_SEVERITY.valued("warning"));
    private static final Tags TAG_SEVERITY_ERROR = Tags.from(TAG_SEVERITY.valued("error"));

    private final HawkularClient inst;

    HawkularLogger(HawkularClient inst) {
        this.inst = inst;
    }

    public String getMetricsBase() {
        return inst.getInfo().getPrefix().orElse("");
    }

    /**
     * Log {@code message} with DEBUG level on logs, and increase related counter
     * @param message the message logged on logs
     */
    public void debug(String message) {
        debug(message, null);
    }

    /**
     * Log {@code message} with DEBUG level on logs, and increase related counter
     * @param message the message logged on logs
     * @param dpTags datapoint tags to associate with this log
     */
    public void debug(String message, Tags dpTags) {
        inst.counter("debug.count", TAG_SEVERITY_DEBUG).inc(dpTags);
        inst.logger("debug.logs", TAG_SEVERITY_DEBUG).log(message, dpTags);
    }

    /**
     * Log an exception with DEBUG level on logs, and increase related counter
     * @param t the exception / throwable
     */
    public void debug(Throwable t) {
        debug(throwableToString(t), Tags.singleton("class", t.getClass().getName()));
    }

    /**
     * Log an exception with DEBUG level on logs, and increase related counter
     * @param t the exception / throwable
     * @param dpTags datapoint tags to associate with this log
     */
    public void debug(Throwable t, Tags dpTags) {
        debug(throwableToString(t), Tags.from(dpTags, Tags.singleton("class", t.getClass().getName())));
    }

    /**
     * Log {@code message} with INFO level on logs, and increase related counter
     * @param message the message logged on logs
     */
    public void info(String message) {
        info(message, null);
    }

    /**
     * Log {@code message} with INFO level on logs, and increase related counter
     * @param message the message logged on logs
     * @param dpTags datapoint tags to associate with this log
     */
    public void info(String message, Tags dpTags) {
        inst.counter("info.count", TAG_SEVERITY_INFO).inc(dpTags);
        inst.logger("info.logs", TAG_SEVERITY_INFO).log(message, dpTags);
    }

    /**
     * Log an exception with INFO level on logs, and increase related counter
     * @param t the exception / throwable
     */
    public void info(Throwable t) {
        info(throwableToString(t), Tags.singleton("class", t.getClass().getName()));
    }

    /**
     * Log an exception with INFO level on logs, and increase related counter
     * @param t the exception / throwable
     * @param dpTags datapoint tags to associate with this log
     */
    public void info(Throwable t, Tags dpTags) {
        info(throwableToString(t), Tags.from(dpTags, Tags.singleton("class", t.getClass().getName())));
    }

    /**
     * Log {@code message} with WARNING level on logs, and increase related counter
     * @param message the message logged on logs
     */
    public void warn(String message) {
        warn(message, null);
    }

    /**
     * Log {@code message} with WARNING level on logs, and increase related counter
     * @param message the message logged on logs
     * @param dpTags datapoint tags to associate with this log
     */
    public void warn(String message, Tags dpTags) {
        inst.counter("warning.count", TAG_SEVERITY_WARNING).inc(dpTags);
        inst.logger("warning.logs", TAG_SEVERITY_WARNING).log(message, dpTags);
    }

    /**
     * Log an exception with WARNING level on logs, and increase related counter
     * @param t the exception / throwable
     */
    public void warn(Throwable t) {
        warn(throwableToString(t), Tags.singleton("class", t.getClass().getName()));
    }

    /**
     * Log an exception with WARNING level on logs, and increase related counter
     * @param t the exception / throwable
     * @param dpTags datapoint tags to associate with this log
     */
    public void warn(Throwable t, Tags dpTags) {
        warn(throwableToString(t), Tags.from(dpTags, Tags.singleton("class", t.getClass().getName())));
    }

    /**
     * Log {@code message} with ERROR level on logs, and increase related counter
     * @param message the message logged on logs
     */
    public void error(String message) {
        error(message, null);
    }

    /**
     * Log {@code message} with ERROR level on logs, and increase related counter
     * @param message the message logged on logs
     * @param dpTags datapoint tags to associate with this log
     */
    public void error(String message, Tags dpTags) {
        inst.counter("error.count", TAG_SEVERITY_ERROR).inc(dpTags);
        inst.logger("error.logs", TAG_SEVERITY_ERROR).log(message, dpTags);
    }

    /**
     * Log an exception with ERROR level on logs, and increase related counter
     * @param t the exception / throwable
     */
    public void error(Throwable t) {
        error(throwableToString(t), Tags.singleton("class", t.getClass().getName()));
    }

    /**
     * Log an exception with ERROR level on logs, and increase related counter
     * @param t the exception / throwable
     * @param dpTags datapoint tags to associate with this log
     */
    public void error(Throwable t, Tags dpTags) {
        error(throwableToString(t), Tags.from(dpTags, Tags.singleton("class", t.getClass().getName())));
    }

    private static String throwableToString(Throwable t) {
        StringBuilder sb = new StringBuilder(t.getClass().getName());
        if (t.getMessage() != null) {
            sb.append(": ").append(t.getMessage());
        }
        return sb.toString();
    }
}
