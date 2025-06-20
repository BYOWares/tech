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

import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import org.gradle.api.GradleException
import org.gradle.internal.extensions.stdlib.uncheckedCast
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * The copyright text to write as a header in every files
 *
 * @since XXX
 */
class Copyright private constructor(
    private val copyright: String
) {
    /**
     * @param i indentation needed (applied on all lines but the first one)
     */
    fun asJavaDoc(i: String): String {
        val s = System.lineSeparator()
        return copyright.lines().joinToString(s, "/*$s", "$s$i */") { l -> i + if (l.isEmpty()) " *" else " * $l" }
    }

    companion object {
        private val UNKNOWN = Copyright("UNKNOWN")

        /**
         * Read the file, and extract the copyright information
         *
         * @param copyrightFile the file containing the copyright text
         */
        fun extractCopyright(copyrightFile: File): Copyright {
            try {
                val copyrightPath = copyrightFile.toPath()
                if (!Files.exists(copyrightPath) || !Files.isRegularFile(copyrightPath)) return UNKNOWN

                val textAsNode = XmlParser().parse(copyrightFile).value() //
                    .uncheckedCast<NodeList>().getAt("option") //
                    .filter { n -> n.uncheckedCast<Node>().get("@name").toString() == "notice" } //
                    .map { n -> n.uncheckedCast<Node>().get("@value").toString() } //
                    .getOrNull(0)

                return if (textAsNode == null) UNKNOWN else Copyright(textAsNode.toString())
            } catch (e: IOException) {
                throw GradleException("Cannot get Copyright content", e)
            }
        }
    }
}
