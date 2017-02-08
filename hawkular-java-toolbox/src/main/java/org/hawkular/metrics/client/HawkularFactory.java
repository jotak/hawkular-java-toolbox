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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.hawkular.metrics.client.common.HawkularClientConfig;
import org.hawkular.metrics.client.config.HawkularYamlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * @author Joel Takvorian
 */
public final class HawkularFactory {

    private static final Logger LOG = LoggerFactory.getLogger(HawkularFactory.class);
    // TODO: change location (system properties?)
    private static final HawkularClientConfig CONFIG = load("hawkular.yaml");

    private HawkularFactory() {
    }

    private static HawkularYamlConfig load(String filename) {
        try {
            Yaml yaml = new Yaml(new Constructor(HawkularYamlConfig.class));
            InputStream input = new FileInputStream(new File(filename));
            return (HawkularYamlConfig) yaml.load(input);
        } catch (IOException e) {
            LOG.error("Could not read Yaml config: ", e);
            HawkularYamlConfig defaultConfig = new HawkularYamlConfig();
            defaultConfig.setTenant("unconfigured");
            return defaultConfig;
        }
    }

    public static HawkularLogger logger(Class<?> clazz) {
        return logger(clazz.getName());
    }

    public static HawkularLogger logger(String owner) {
        return HawkularClientBuilder.fromConfig(CONFIG)
                .addGlobalTag("owner", owner)
                .prefixedWith(owner + ".")
                .buildLogger();
    }

    public static HawkularClient create() {
        return HawkularClientBuilder.fromConfig(CONFIG).build();
    }

    public static HawkularClientBuilder builder() {
        return HawkularClientBuilder.fromConfig(CONFIG);
    }
}
