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

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @since XXX
 */
abstract class CopyLog4JFileTask : DefaultTask() {
    init {
        group = BasePlugin.BUILD_GROUP
        description = "Copy the Log4J2 configuration from plugin to project."
    }

    @get:Input
    val forTest: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)

    @get:InputFile
    abstract var log4J2ConfigFile: Provider<File>

    @get:OutputDirectory
    val outputDirectory: DirectoryProperty = project.objects.directoryProperty().convention(
        project.layout.projectDirectory.dir("src").dir("main").dir("resources")
    )

    @TaskAction
    fun copyFile() {
        val file = log4J2ConfigFile.get()
        val suffix = if (forTest.get()) "-test" else ""
        val newFileName = newFileName(forTest.get(), file.name)
        val updatedContent = file.readText().replace("PROJECT_NAME", project.name + suffix)
        outputDirectory.get().asFile.resolve(newFileName).writeText(updatedContent)
    }

    fun newFileName(
        isForTest: Boolean,
        fileName: String
    ): String {
        if (!isForTest) return fileName
        val lastIndex = fileName.lastIndexOf('.')
        if (lastIndex < 0) return fileName + "-test"
        return "" + fileName.subSequence(0, lastIndex) + "-test" + fileName.subSequence(lastIndex, fileName.length)
    }
}
