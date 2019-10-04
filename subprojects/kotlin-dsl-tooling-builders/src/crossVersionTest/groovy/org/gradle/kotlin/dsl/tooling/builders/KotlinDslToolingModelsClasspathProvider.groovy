/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.kotlin.dsl.tooling.builders

import groovy.transform.CompileStatic
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.artifacts.DependencyResolutionServices
import org.gradle.integtests.fixtures.executer.GradleDistribution
import org.gradle.integtests.tooling.fixture.ToolingApiAdditionalClasspathProvider
import org.gradle.integtests.tooling.fixture.ToolingApiDistribution
import org.gradle.util.GradleVersion


/**
 * Provides TAPI client additional classpath as found in Kotlin IDEs.
 */
@CompileStatic
class KotlinDslToolingModelsClasspathProvider implements ToolingApiAdditionalClasspathProvider {

    @Override
    List<File> additionalClasspathFor(
        DependencyResolutionServices resolutionServices,
        ToolingApiDistribution toolingApi,
        GradleDistribution gradle
    ) {
        return additionalClasspathFrom(resolutionServices, toolingApi) +
            additionalClasspathFrom(gradle)
    }

    private static List<File> additionalClasspathFrom(
        DependencyResolutionServices resolutionServices,
        ToolingApiDistribution toolingApi
    ) {
        if (toolingApi.version.baseVersion >= GradleVersion.version("6.0")) {
            return []
        }
        if (toolingApi.version.baseVersion >= GradleVersion.version("4.1")) {
            return resolveDependencies(resolutionServices,
                "org.gradle:gradle-kotlin-dsl-tooling-models:${kotlinDslVersionOf(toolingApi)}"
            )
        }
        throw unsupportedToolingApiVersion(toolingApi)
    }

    private static List<File> additionalClasspathFrom(GradleDistribution gradle) {
        return gradle.gradleHomeDir.file("lib").listFiles().findAll { file ->
            file.name.startsWith("gradle-kotlin-dsl-") || file.name.startsWith("kotlin-stdlib-")
        }.collect { it.canonicalFile }.tap { classpath ->
            assert classpath.size() >= 3
        }
    }

    private static List<File> resolveDependencies(DependencyResolutionServices resolutionServices, String... notations) {
        return resolutionServices.with {
            configurationContainer.detachedConfiguration(
                notations.collect { dependencyHandler.create(it) } as Dependency[]
            ).files as List
        }
    }

    private static String kotlinDslVersionOf(ToolingApiDistribution toolingApi) {
        GradleVersion minorVersion = GradleVersion.version(toolingApi.version.baseVersion.version.take(3))
        String kotlinDslVersion = VERSION_HISTORY[minorVersion.version]
        if (kotlinDslVersion == null) {
            throw unsupportedToolingApiVersion(toolingApi)
        }
        return kotlinDslVersion
    }

    private static Map<String, String> VERSION_HISTORY = [
        "5.6": "5.6",
        "5.5": "5.5",
        "5.4": "5.4",
        "5.3": "5.3",
        "5.2": "1.1.3",
        "5.1": "1.1.1",
        "5.0": "1.0.4",
        "4.10": "1.0-rc-6",
        "4.9": "0.18.4",
        "4.8": "0.17.5",
        "4.7": "0.16.3",
        "4.6": "0.15.6",
        "4.5": "0.14.2",
        "4.4": "0.13.2",
        "4.3": "0.12.3",
        "4.2": "0.11.1",
        "4.1": "0.10.3"
    ]

    private static UnsupportedOperationException unsupportedToolingApiVersion(ToolingApiDistribution toolingApi) {
        new UnsupportedOperationException("Unsupported Tooling API version ${toolingApi.version.version}")
    }
}
