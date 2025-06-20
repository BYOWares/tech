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

import org.gradle.internal.extensions.stdlib.uncheckedCast

plugins {
    id("byogame.java-common")
    id("org.openjfx.javafxplugin")
}

javafx {
    val javaPluginExtension = project.extensions.findByName("java")!!.uncheckedCast<JavaPluginExtension>()
    version = javaPluginExtension.toolchain.languageVersion.get().toString()
    modules = listOf("base", "graphics", "controls", "fxml", "media").stream().map { x -> "javafx.$x"; }.toList()
}

dependencies {
    implementation("io.github.mkpaz:atlantafx-base:${project.ext.get("atlantafx")}")

    implementation("org.controlsfx:controlsfx:${project.ext.get("controlsfx")}")

    val ikonli = project.ext.get("ikonli")
    implementation("org.kordamp.ikonli:ikonli-bootstrapicons-pack:$ikonli")
    implementation("org.kordamp.ikonli:ikonli-core:$ikonli")
    implementation("org.kordamp.ikonli:ikonli-feather-pack:$ikonli")
    implementation("org.kordamp.ikonli:ikonli-javafx:$ikonli")
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:$ikonli")
}
