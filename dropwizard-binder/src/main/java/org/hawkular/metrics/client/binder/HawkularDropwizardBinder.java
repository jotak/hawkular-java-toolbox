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
package org.hawkular.metrics.client.binder;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.hawkular.metrics.client.config.HawkularClientInfo;
import org.hawkular.metrics.dropwizard.HawkularReporter;
import org.hawkular.metrics.dropwizard.HawkularReporterBuilder;

import com.codahale.metrics.MetricRegistry;

/**
 * @author Joel Takvorian
 */
public final class HawkularDropwizardBinder {

    private HawkularDropwizardBinder() {
    }

    /**
     * Bind an Hawkular Toolbox configuration to Dropwizard's {@link HawkularReporter}
     * @param dropwizardRegistry the dropwizard metrics registry that will be used for {@link HawkularReporter}
     * @param clientInfo the {@link HawkularClientInfo} from toolbox
     * @param period reporter's polling period
     * @param unit reporter's polling period time unit
     * @return the new {@link HawkularReporter}
     */
    public static HawkularReporter bind(MetricRegistry dropwizardRegistry, HawkularClientInfo clientInfo, long period,
                                        TimeUnit unit) {
        HawkularReporterBuilder builder = HawkularReporter.builder(dropwizardRegistry, clientInfo.getTenant())
                .globalTags(clientInfo.getGlobalTags())
                .perMetricTags(clientInfo.getPerMetricTags())
                .useHttpClient(uri -> clientInfo.getHttpClient());
        clientInfo.getPrefix().ifPresent(builder::prefixedWith);
        clientInfo.getRegexTags().forEach(regexTag -> {
            Pattern pattern = regexTag.getRegex();
            regexTag.getTags().forEach((k,v) -> builder.addRegexTag(pattern, k, v));
        });

        HawkularReporter hawkularReporter = builder.build();
        hawkularReporter.start(period, unit);
        return hawkularReporter;
    }
}
