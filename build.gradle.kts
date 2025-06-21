/*
 * Copyright BYOWares
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("byowares")
    id("byogame.version")
}

byowares {
    val rawSuffix =
        project.properties["BUILD_SUFFIX"]?.toString() ?: if (project.hasProperty("BUILD_RELEASE")) "" else "SNAPSHOT"
    val suffix = if (rawSuffix.isEmpty() || rawSuffix.startsWith("-")) rawSuffix else "-$rawSuffix"

    versionSuffix.set(project.providers.provider { suffix })
    versionsFile.set(rootProject.layout.projectDirectory.file("versions.yml"))
    copyrightFile.set(rootProject.layout.projectDirectory.file(".idea/copyright/BYOWares.xml"))
    log4J2ConfigFile.set(rootProject.layout.projectDirectory.file("buildSrc/src/main/resources/log4j2.xml"))
}
