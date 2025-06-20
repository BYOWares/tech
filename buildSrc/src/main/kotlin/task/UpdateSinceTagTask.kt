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
package task

import model.Version
import model.Versions
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @since XXX
 */
abstract class UpdateSinceTagTask : DefaultTask() {
    companion object {
        const val UPDATE_SINCE_TAG_TASK_NAME = "updateSinceTag"
    }

    init {
        group = "Release"
        description =
            "Update the tag '@since ${Version.UNKNOWN}' in all files found in the given directory by the version to publish."
    }

    @get:InputDirectory
    val inputDir: DirectoryProperty = project.objects.directoryProperty().convention(
        project.layout.projectDirectory.dir("src")
    )

    @get:Input
    val updateVersionsFile: Property<Boolean> = project.objects.property(Boolean::class.java)

    @get:InputFile
    abstract var versionsFile: Provider<File>

    @TaskAction
    fun updateFiles() {
        val versions = Versions.parse(versionsFile.get())
        val versionToPublish = versions.getVersionToPublish()
        inputDir.get().asFile.walk().forEach { f ->
            if (f.isDirectory) return@forEach
            val text = f.readText()
            val toReplace = "@since ${Version.UNKNOWN}"
            if (!text.contains(toReplace)) return@forEach
            val updatedContent = f.readText().replace(toReplace, "@since $versionToPublish")
            f.writeText(updatedContent)
        }
        if (updateVersionsFile.get()) versions.updateUnknownVersionToNextVersionAndDumpFile()
    }
}
