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

import model.GitInfo
import model.Versions
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import java.io.File
import java.time.Instant

/**
 * @since XXX
 */
abstract class GenerateJavaInfoFileTask : AbstractGenerateInfoFileTask() {
    init {
        description = "Generate a file with some information about the library."
    }

    @Input
    val className = project.name.split("-").stream() //
        .map { p: String -> p[0].uppercaseChar() + p.substring(1) } //
        .toList().joinToString("") + "Info"

    @Internal
    val timestamp = Instant.now().toEpochMilli()

    @Input
    val revision = GitInfo.gitInfo(project.gradle.rootProject.rootDir).revision

    @get:Input
    abstract var versionSuffix: Provider<String>

    override val outputFile: File
        get() = resolveOutputFile(className)

    override fun generateBody(versions: Versions): String {
        return """
            // Do not edit this generated file (see GenerateJavaInfoFileTask)
            package ${packageName()};

            /**
             * Information related to the <b>${moduleName()}</b> module
             *
             * @since ${versions.getModuleFirstVersion(project.name)}
             */
            public final class $className {
                /** Version of the module */
                public static final String VERSION = "${versions.getVersionToPublish()}${versionSuffix.get()}";
                /** Module build timestamp in milliseconds */
                public static final String BUILD_TIMESTAMP = "$timestamp";
                /** Current revision while building the module */
                public static final String REVISION = "$revision";
                /** A concatenation of all pieces of information */
                public static final String TO_STRING = "$className [version=" + VERSION + ", build-timestamp=" + BUILD_TIMESTAMP + ", revision=" + REVISION + "]";

                private $className() {
                    // Utility class
                }
            }
            """.trimIndent()
    }
}
