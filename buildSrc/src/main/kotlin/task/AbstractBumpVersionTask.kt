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
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @since XXX
 */
abstract class AbstractBumpVersionTask : DefaultTask() {
    init {
        group = "Release"
    }

    @get:InputFile
    abstract var versionsFile: Provider<File>

    abstract fun bump(versions: Versions)

    @TaskAction
    fun bumpVersion() {
        bump(Versions.parse(versionsFile.get()))
    }

    abstract class BumpMajorVersionTask : AbstractBumpVersionTask() {
        init {
            description = "Increase the major version to publish by 1 (reset minor and patch to 0)."
        }

        override fun bump(versions: Versions) {
            versions.bumpNextMajorVersionAndDumpFile()
        }
    }

    abstract class BumpMinorVersionTask : AbstractBumpVersionTask() {
        init {
            description = "Increase the minor version to publish by 1 (leave major untouched, and reset patch to 0)."
        }

        override fun bump(versions: Versions) {
            versions.bumpNextMinorVersionAndDumpFile()
        }
    }

    abstract class BumpPatchVersionTask : AbstractBumpVersionTask() {
        init {
            description = "Increase the patch version to publish by 1 (leave major and minor untouched)."
        }

        override fun bump(versions: Versions) {
            versions.bumpNextPatchVersionAndDumpFile()
        }
    }
}
