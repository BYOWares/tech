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

import extension.BYOWaresExtension
import org.gradle.internal.extensions.stdlib.uncheckedCast
import task.CopyLog4JFileTask
import task.GenerateJavaInfoFileTask
import task.GeneratePackageInfoFileTask
import task.SanitizeVersionsFileTask
import task.UpdateSinceTagTask

plugins {
    id("byogame.catalog")
    java
}

repositories {
    mavenCentral()
}

configurations.configureEach { isTransitive = false }

dependencies {
    /* TESTING DEPENDENCIES */
    val junit = project.ext.get("junit")
    testImplementation("org.junit.jupiter:junit-jupiter:${junit}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junit}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junit}")
    testImplementation("org.apiguardian:apiguardian-api:${project.ext.get("guardian")}")

    val jPlatform = project.ext.get("jPlatform")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junit}")
    testRuntimeOnly("org.junit.platform:junit-platform-commons:${jPlatform}")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:${jPlatform}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:${jPlatform}")
    testRuntimeOnly("org.opentest4j:opentest4j:${project.ext.get("opentest4j")}")

    /* LOGGING DEPENDENCIES */
    val slf4j = project.ext.get("slf4j")
    implementation("org.slf4j:slf4j-api:${slf4j}")

    val log4j = project.ext.get("log4j")
    runtimeOnly("org.slf4j:jcl-over-slf4j:${slf4j}")
    runtimeOnly("org.slf4j:jul-to-slf4j:${slf4j}")
    runtimeOnly("org.slf4j:log4j-over-slf4j:${slf4j}")
    runtimeOnly("org.apache.logging.log4j:log4j-api:${log4j}")
    runtimeOnly("org.apache.logging.log4j:log4j-core:${log4j}")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl:${log4j}")
//    runtimeOnly("com.lmax:disruptor:${project.ext.get("disruptor")}")

    /* UTIL DEPENDENCIES */
    implementation("org.agrona:agrona:${project.ext.get("agrona")}")
}

// Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

sourceSets {
    main {
        java {
            srcDir("src-generated/main/java")
        }
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

val genJavaInfoFile = "generateJavaInfoFile"
val genPkgInfoFile = "generatePackageInfoFile"
val copyLog4J2Conf4Test = "copyLog4J2Conf4Test"
val byoExt = rootProject.extensions.getByType(BYOWaresExtension::class.java)

tasks.register<GeneratePackageInfoFileTask>(genPkgInfoFile) {
    groupId = byoExt.groupId
    baseProjectName = byoExt.baseProjectName
    versionsFile = byoExt.versionsFile.asFile
    copyrightFile = byoExt.copyrightFile.asFile
}

tasks.register<GenerateJavaInfoFileTask>(genJavaInfoFile) {
    groupId = byoExt.groupId
    baseProjectName = byoExt.baseProjectName
    versionsFile = byoExt.versionsFile.asFile
    copyrightFile = byoExt.copyrightFile.asFile
    versionSuffix = byoExt.versionSuffix
}

tasks.register<UpdateSinceTagTask>(UpdateSinceTagTask.UPDATE_SINCE_TAG_TASK_NAME) {
    versionsFile = byoExt.versionsFile.asFile
    updateVersionsFile.set(false)
}

tasks.register<CopyLog4JFileTask>(copyLog4J2Conf4Test) {
    log4J2ConfigFile = byoExt.log4J2ConfigFile.asFile
    forTest.set(true)
    outputDirectory.set(project.layout.projectDirectory.dir("src").dir("test").dir("resources"))
}

tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME) { dependsOn(genJavaInfoFile) }
tasks.named(genJavaInfoFile) { dependsOn(genPkgInfoFile) }
tasks.named(genJavaInfoFile) { dependsOn(rootProject.tasks.named(SanitizeVersionsFileTask.SANITIZE_VERSIONS_FILE_TASK_NAME)) }
tasks.named(JavaPlugin.PROCESS_TEST_RESOURCES_TASK_NAME) { dependsOn(copyLog4J2Conf4Test) }

tasks.findByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
    ?.uncheckedCast<JavaCompile>()?.options?.compilerArgs?.add("-Xlint:unchecked")
