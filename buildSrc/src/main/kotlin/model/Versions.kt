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
package model

import org.gradle.api.GradleException
import org.gradle.internal.extensions.stdlib.uncheckedCast
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.function.Function

/**
 * Model of a versions file, which contains:
 * * the next version to publish;
 * * the first version for each module.
 *
 * @since XXX
 */
class Versions private constructor(
    private val file: File,
    private var versionToPublish: Version,
    private val modulesFirstVersion: SortedMap<String, Version>
) {
    fun getVersionToPublish(): Version {
        return versionToPublish
    }

    /**
     * @return the first version associated to the module (the project.name)
     */
    fun getModuleFirstVersion(module: String): Version? {
        return modulesFirstVersion[module]
    }

    /**
     * Increase major version of the next version to publish, and update the versions file.
     * @see Version.bumpNextMajor
     */
    fun bumpNextMajorVersionAndDumpFile() {
        bumpVersion(Version::bumpNextMajor)
    }

    /**
     * Increase minor version of the next version to publish, and update the versions file.
     * @see Version.bumpNextMinor
     */
    fun bumpNextMinorVersionAndDumpFile() {
        bumpVersion(Version::bumpNextMinor)
    }

    /**
     * Increase patch version of the next version to publish, and update the versions file/
     * @see Version.bumpNextPatch
     */
    fun bumpNextPatchVersionAndDumpFile() {
        bumpVersion(Version::bumpNextPatch)
    }

    private fun bumpVersion(updateFunction: Function<Version, Version>) {
        versionToPublish = updateFunction.apply(versionToPublish)
        dumpFile()
    }

    /**
     * Add missing
     * @param modules the complete list of modules (projects' name)
     */
    fun sanitizeModules(modules: Set<String>) {
        modules.forEach { m -> modulesFirstVersion.putIfAbsent(m, Version.UNKNOWN) }
        modulesFirstVersion.keys.removeIf { m -> !modules.contains(m) }
        dumpFile()
    }

    fun updateUnknownVersionToNextVersionAndDumpFile() {
        for (key in modulesFirstVersion.keys) {
            if (modulesFirstVersion[key] == Version.UNKNOWN) modulesFirstVersion[key] = versionToPublish
        }
        dumpFile()
    }

    private fun dumpFile() {
        val data = mutableMapOf<String, Any>()
        data[VERSION_TO_PUBLISH_KEY] = versionToPublish.toString()
        data[MODULES_FIRST_VERSION_KEY] = modulesFirstVersion.mapValues { (_, v) -> v.toString() }.toSortedMap()

        val options = DumperOptions()
        options.indent = 2
        options.isPrettyFlow = true
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK

        FileWriter(file).use { fw -> Yaml(options).dump(data, fw) }
    }

    companion object {
        private const val VERSION_TO_PUBLISH_KEY = "versionToPublish"
        private const val MODULES_FIRST_VERSION_KEY = "modulesFirstVersion"

        /**
         * Parse the file and return and object representing it
         *
         * @param versionsFile the versions file to parse
         */
        fun parse(versionsFile: File): Versions {
            try {
                val versionsPath = versionsFile.toPath()
                if (!Files.exists(versionsPath) || !Files.isRegularFile(versionsPath)) {
                    return Versions(versionsFile, Version.UNKNOWN, TreeMap())
                }

                var map: Map<String, Any>
                FileInputStream(versionsFile).use { fis -> map = Yaml().load(fis) }
                val versionToPublish = map.getOrDefault(VERSION_TO_PUBLISH_KEY, Version.UNKNOWN).toString() //
                val modulesFirstVersion = map.getOrDefault(MODULES_FIRST_VERSION_KEY, mutableMapOf<String, String>()) //
                    .uncheckedCast<Map<String, String>>() //
                    .mapValues { (_, v) -> Version.parse(v) } //
                    .toSortedMap()

                return Versions(versionsFile, Version.parse(versionToPublish), modulesFirstVersion)
            } catch (e: IOException) {
                throw GradleException("Cannot get versions content", e)
            }
        }
    }
}
