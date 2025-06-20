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

import model.Copyright
import model.Versions
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @since XXX
 */
abstract class AbstractGenerateInfoFileTask : DefaultTask() {
    init {
        group = BasePlugin.BUILD_GROUP
    }

    @get:Input
    abstract var groupId: Provider<String>

    @get:Input
    abstract var baseProjectName: Provider<String>

    @get:InputFile
    abstract var copyrightFile: Provider<File>

    @get:InputFile
    abstract var versionsFile: Provider<File>

    @get:OutputFile
    abstract val outputFile: File

    abstract fun generateBody(versions: Versions): String

    fun resolveOutputFile(fileName: String): File {
        return project.projectDir.resolve("src-generated/main/java/${packageName().replace('.', '/')}/$fileName.java")
    }

    fun moduleName(): String {
        return "${groupId.get()}.${baseProjectName.get()}.${project.name.replace('-', '.')}"
    }

    fun packageName(): String {
        return "${moduleName()}.info"
    }

    @TaskAction
    fun generateInfoFile() {
        val indent = "            "
        val copyright = Copyright.extractCopyright(copyrightFile.get()).asJavaDoc(indent)
        val versions = Versions.parse(versionsFile.get())
        val s = System.lineSeparator()
        // Starting by an empty new line to have a clean indentation.
        val body = (s + generateBody(versions)).lines().joinToString(s) { l -> if (l.isEmpty()) l else "$indent$l" }
        outputFile.parentFile.mkdirs()
        outputFile.createNewFile()
        outputFile.writeText(
            """
            $copyright
            $body
            """.trimIndent()
        )
    }
}
