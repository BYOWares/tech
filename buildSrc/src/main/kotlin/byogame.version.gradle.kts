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
import task.AbstractBumpVersionTask.BumpMajorVersionTask
import task.AbstractBumpVersionTask.BumpMinorVersionTask
import task.AbstractBumpVersionTask.BumpPatchVersionTask
import task.SanitizeVersionsFileTask
import task.UpdateSinceTagTask

plugins {
    id("byowares")
}

val byoExt = rootProject.extensions.getByType(BYOWaresExtension::class.java)

tasks.register<BumpMajorVersionTask>("bumpMajorVersion") {
    versionsFile = byoExt.versionsFile.asFile
}
tasks.register<BumpMinorVersionTask>("bumpMinorVersion") {
    versionsFile = byoExt.versionsFile.asFile
}
tasks.register<BumpPatchVersionTask>("bumpPatchVersion") {
    versionsFile = byoExt.versionsFile.asFile
}
tasks.register<SanitizeVersionsFileTask>(SanitizeVersionsFileTask.SANITIZE_VERSIONS_FILE_TASK_NAME) {
    versionsFile = byoExt.versionsFile.asFile
}
tasks.register<UpdateSinceTagTask>(UpdateSinceTagTask.UPDATE_SINCE_TAG_TASK_NAME) {
    versionsFile = byoExt.versionsFile.asFile
    inputDir.set(project.layout.projectDirectory.dir("buildSrc").dir("src"))
    updateVersionsFile.set(true)
}
