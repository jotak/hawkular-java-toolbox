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

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hawkular.metrics.client.common.HawkularClientConfig;
import org.hawkular.metrics.client.config.HawkularYamlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * @author Joel Takvorian
 */
public class HawkularFactory {

    private static final Logger LOG = LoggerFactory.getLogger(HawkularFactory.class);
    private static final String PROPKEY_CONFIG_FILE = "hawkular.java.toolbox.config";
    private static final String DEFAULT_FILE_PATH = "hawkular.yaml";
    private static final List<Variable> VARIABLES = new ArrayList<>();

    static {
        VARIABLES.add(new Variable(Pattern.compile("\\$\\{host}"), () -> {
            try {
                return InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                return "?";
            }
        }));
    }

    private final HawkularClientConfig config;

    private HawkularFactory(HawkularClientConfig config) {
        this.config = config;
    }

    /**
     * Initialize the factory with default config file.<br/>
     * First, it will look for YAML file path given by system property "hawkular.java.toolbox.config"<br/>
     * If not found, it looks for "hawkular.yaml" from current working dir<br/>
     * Or finally, loads a default, unconfigured Hawkular client config
     */
    public static HawkularFactory load() {
        return new HawkularFactory(loadConfig(Optional.ofNullable(System.getProperty(PROPKEY_CONFIG_FILE))));
    }

    /**
     * Initialize the factory with the input YAML config file.<br/>
     * If not found, loads a default, unconfigured Hawkular client config
     */
    public static HawkularFactory loadFrom(String configFilePath) {
        return new HawkularFactory(loadConfig(Optional.of(configFilePath)));
    }

    private static HawkularYamlConfig loadConfig(Optional<String> filepath) {
        try {
            File configFile = findConfigurationFile(filepath);
            if (configFile == null) {
                LOG.warn("Configuration file not found");
                return loadUnconfigured();
            }
            Yaml yaml = new Yaml(new Constructor(HawkularYamlConfig.class));
            InputStream input = new FileInputStream(configFile);
            return replaceVariables((HawkularYamlConfig) yaml.load(input));
        } catch (IOException e) {
            LOG.error("Could not read Yaml config: ", e);
            return loadUnconfigured();
        }
    }

    private static HawkularYamlConfig replaceVariables(HawkularYamlConfig config) {
        if (config.getTenant() != null) {
            config.setTenant(replaceVariables(config.getTenant()));
        }
        if (config.getPrefix() != null) {
            config.setPrefix(replaceVariables(config.getPrefix()));
        }
        if (config.getGlobalTags() != null) {
            Map<String, String> replaced = config.getGlobalTags().entrySet().stream()
                    .collect(toMap(
                            Map.Entry::getKey,
                            e -> replaceVariables(e.getValue())
                    ));
            config.setGlobalTags(replaced);
        }
        if (config.getPerMetricTags() != null) {
            Map<String, Map<String, String>> replaced = config.getPerMetricTags().entrySet().stream()
                    .collect(toMap(
                            Map.Entry::getKey,
                            tags -> tags.getValue().entrySet().stream()
                                .collect(toMap(
                                        Map.Entry::getKey,
                                        e -> replaceVariables(e.getValue())
                                ))));
            config.setPerMetricTags(replaced);
        }
        return config;
    }

    private static String replaceVariables(String source) {
        for (Variable variable : VARIABLES) {
            source = variable.replace(source);
        }
        return source;
    }

    private static HawkularYamlConfig loadUnconfigured() {
        HawkularYamlConfig defaultConfig = new HawkularYamlConfig();
        defaultConfig.setTenant("unconfigured");
        return defaultConfig;
    }

    private static File findConfigurationFile(Optional<String> filepath) throws IOException {
        File file = new File(filepath.orElse(DEFAULT_FILE_PATH));
        if (!filepath.isPresent() && !file.exists()) {
            return null;
        }
        if (!file.isFile()) {
            throw new IOException(file + " is not a regular file");
        }
        if (!file.canRead()) {
            throw new IOException(file + " is not readable");
        }
        return file;
    }

    /**
     * Creates an Hawkular logger for the input class. Each log affects the following metrics:<br/>
     *     <ul>
     *         <li><i>[class name].[debug|info|warning|error].count</i>, counts the number of occurrences</li>
     *         <li><i>[class name].[debug|info|warning|error].timeline</i>, a String metric that stores the message</li>
     *     </ul>
     *     All metrics have the following tags: <br/>
     *     <ul>
     *         <li>class: [class full name]</li>
     *         <li>severity: [debug|info|warning|error]</li>
     *     </ul>
     * @param clazz related class
     * @return the logger
     */
    public HawkularLogger logger(Class<?> clazz) {
        return HawkularClientBuilder.fromConfig(config).buildLogger(clazz);
    }

    /**
     * Creates an Hawkular logger for the input source. Each log affects the following metrics:<br/>
     *     <ul>
     *         <li><i>[input source].[debug|info|warning|error].count</i>, counts the number of occurrences</li>
     *         <li><i>[input source].[debug|info|warning|error].timeline</i>, a String metric that stores the message</li>
     *     </ul>
     *     All metrics have the following tags: <br/>
     *     <ul>
     *         <li>source: [input source]</li>
     *         <li>severity: [debug|info|warning|error]</li>
     *     </ul>
     * @param source the source name
     * @return the logger
     */
    public HawkularLogger logger(String source) {
        return HawkularClientBuilder.fromConfig(config).buildLogger(source);
    }

    /**
     * Creates an {@link HawkularClient} instance, using the relevant configuration
     * @return a new {@link HawkularClient}
     */
    public HawkularClient create() {
        return HawkularClientBuilder.fromConfig(config).build();
    }

    /**
     * Creates an {@link HawkularClient} builder, pre-configured using the relevant configuration.
     * This builder can be used to override the default configuration and build a new {@link HawkularClient}.
     * @return an {@link HawkularClientBuilder}
     */
    public HawkularClientBuilder builder() {
        return HawkularClientBuilder.fromConfig(config);
    }

    private static class Variable {
        private final Pattern pattern;
        private final Supplier<String> replacementSupplier;

        private Variable(Pattern pattern, Supplier<String> replacementSupplier) {
            this.pattern = pattern;
            this.replacementSupplier = replacementSupplier;
        }

        private String replace(String source) {
            String replacement = replacementSupplier.get();
            Matcher matcher = pattern.matcher(source);
            return matcher.replaceAll(replacement);
        }
    }
}
