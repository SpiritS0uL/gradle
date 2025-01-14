/*
 * Copyright 2018 the original author or authors.
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
package org.gradle.kotlin.dsl.provider

import org.gradle.kotlin.dsl.tooling.models.KotlinDslModelsParameters


fun inClassPathMode() =
    providerModeSystemPropertyValue in listOf(
        KotlinDslModelsParameters.CLASSPATH_MODE_SYSTEM_PROPERTY_VALUE,
        KotlinDslModelsParameters.STRICT_CLASSPATH_MODE_SYSTEM_PROPERTY_VALUE
    )


fun inLenientMode() =
    providerModeSystemPropertyValue == KotlinDslModelsParameters.CLASSPATH_MODE_SYSTEM_PROPERTY_VALUE


private
val providerModeSystemPropertyValue: String?
    get() = System.getProperty(KotlinDslModelsParameters.PROVIDER_MODE_SYSTEM_PROPERTY_NAME)
