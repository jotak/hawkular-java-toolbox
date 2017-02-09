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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Map;
import java.util.regex.Pattern;

import org.hawkular.metrics.client.config.HawkularClientInfo;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Joel Takvorian
 */
public class HawkularFactoryTest {

    private static final String KEY = "hawkular.java.toolbox.config";

    @Before
    public void resetProps() {
        System.clearProperty(KEY);
    }

    @Test
    public void shouldCreateDefault() {
        HawkularClientInfo info = HawkularFactory.load().create().getInfo();
        assertThat(info.getTenant()).isEqualTo("unconfigured");
    }

    @Test
    public void shouldCreateFromSystemProperty() {
        System.setProperty(KEY, "src/test/resources/hawkular1.yaml");
        HawkularClientInfo info = HawkularFactory.load().create().getInfo();
        assertThat(info.getTenant()).isEqualTo("cyrus");
        assertThat(info.getPrefix()).hasValue("${host}.");
        assertThat(info.getGlobalTags()).containsOnly(
                entry("hostname", "${host}"),
                entry("owner", "jdoe"));
        assertThat(info.getPerMetricTags()).isEmpty();
    }

    @Test
    public void shouldCreateFromExplicitPath() {
        HawkularClientInfo info = HawkularFactory.loadFrom("src/test/resources/hawkular2.yaml").create().getInfo();
        assertThat(info.getTenant()).isEqualTo("darius");
        assertThat(info.getPrefix()).hasValue("${host}.");
        assertThat(info.getPerMetricTags()).containsOnlyKeys("guava.cache.read");
        Map<String, String> tags = info.getPerMetricTags().get("guava.cache.read");
        assertThat(tags).containsOnly(entry("impl", "guava"));
        assertThat(info.getGlobalTags()).isEmpty();
        assertThat(info.getRegexTags()).extracting(r -> r.getRegex().toString()).containsExactly(Pattern.compile("ehcache\\..*").toString());
        assertThat(info.getRegexTags().iterator().next().getTags()).containsOnly(entry("impl", "ehcache"));
    }
}
