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

import model.Versions
import java.io.File

/**
 * @since XXX
 */
abstract class GeneratePackageInfoFileTask : AbstractGenerateInfoFileTask() {
    init {
        description = "Generate a package-info file with some information about the package."
    }

    override val outputFile: File
        get() = resolveOutputFile("package-info")

    override fun generateBody(versions: Versions): String {
        return """
            // Do not edit this generated file (see GeneratePackageInfoFileTask)

            /**
             * Information package about the <b>${moduleName()}</b> module
             *
             * @since ${versions.getModuleFirstVersion(project.name)}
             */
            package ${packageName()};
            """.trimIndent()
    }
}
