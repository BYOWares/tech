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
package extension

import org.gradle.api.Project
import javax.inject.Inject

/**
 * Configuration shared throughout all plugins and tasks
 *
 * @since XXX
 */
abstract class BYOWaresExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    /** The group identifier used to start the package (by default “fr.byowares”) */
    val groupId = objects.property(String::class.java).convention("fr.byowares")

    /** The name of the root project (defaults to “rootProject.name”)  */
    val baseProjectName = objects.property(String::class.java).convention(project.rootProject.name)

    /** The suffix used for the current version (-SNAPSHOT, -rc1, etc.) */
    val versionSuffix = objects.property(String::class.java)

    /** The file containing all versions */
    val versionsFile = objects.fileProperty()

    /** The intellij configuration file containing the copyright */
    val copyrightFile = objects.fileProperty()

    /** The Log4j2 configuration file */
    val log4J2ConfigFile = objects.fileProperty()

    companion object {
        const val EXTENSION_NAME = "byowares"
    }
}
