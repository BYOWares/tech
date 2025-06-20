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
import org.gradle.api.logging.Logging

import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * The current Git revision
 *
 * @since XXX
 */
class GitInfo private constructor(
    internal val revision: String
) {

    companion object {
        private const val UNKNOWN: String = "UNKNOWN"
        private const val HEAD: String = "HEAD"
        private const val GIT_DIR: String = "gitdir:"
        private const val COMMON_DIR: String = "commondir"
        private const val REF: String = "ref:"
        private const val PACKED_REFS: String = "packed-refs"

        /**
         * Extract the git current revision
         *
         * See https://git-scm.com/docs/gitrepository-layout and https://git-scm.com/docs/git-worktree.
         *
         * @param rootDir the root directory of the git project
         */
        fun gitInfo(rootDir: File): GitInfo {
            try {
                val dotGit = rootDir.toPath().resolve(".git")
                val revision: String
                if (!Files.exists(dotGit)) {
                    return GitInfo(UNKNOWN)
                }
                val head: Path
                val gitDir: Path
                if (Files.isDirectory(dotGit)) {
                    // Git repository, we can read HEAD directly
                    head = dotGit.resolve(HEAD)
                    gitDir = dotGit
                } else {
                    // Git worktree/submodule: follow the pointer to the repository
                    val reference = Paths.get(readFirstLine(dotGit).substring(GIT_DIR.length).trim { it <= ' ' })
                    if (reference.parent.endsWith("modules")) {
                        // Git submodule: follow the reference to the git repository
                        gitDir = rootDir.toPath().resolve(reference)
                        head = gitDir.resolve(HEAD)
                    } else {
                        // Worktree: resolve the root repo directory
                        if (!Files.exists(reference)) {
                            return GitInfo(UNKNOWN)
                        }
                        head = reference.resolve(HEAD)
                        val commonDir = Paths.get(readFirstLine(reference.resolve(COMMON_DIR)))
                        gitDir = if (commonDir.isAbsolute) {
                            commonDir
                        } else {
                            // Common case
                            reference.resolve(commonDir)
                        }
                    }
                }
                val ref = readFirstLine(head)
                if (ref.startsWith(REF)) {
                    val refName = ref.substring(REF.length).trim { it <= ' ' }
                    val refFile = gitDir.resolve(refName)
                    if (Files.exists(refFile)) {
                        revision = readFirstLine(refFile)
                    } else if (Files.exists(gitDir.resolve(PACKED_REFS))) {
                        // Check packed references for commit ID
                        val p = Pattern.compile("^([a-f0-9]{40}) $refName$")
                        Files.lines(gitDir.resolve(PACKED_REFS)).use { lines ->
                            revision = lines //
                                .map { input: String -> p.matcher(input) } //
                                .filter { obj: Matcher -> obj.matches() } //
                                .map { m: Matcher -> m.group(1) }.findFirst() //
                                .orElseThrow { IOException("Packed reference not found for refName $refName") }
                        }
                    } else {
                        val refsDir = gitDir.resolve("refs").toFile()
                        if (refsDir.exists()) {
                            val ls = System.lineSeparator()
                            val foundRefs = Arrays.stream(refsDir.listFiles()).map { f: File -> f.name } //
                                .collect(Collectors.joining(ls))
                            Logging.getLogger(GitInfo::class.java).error("Found git refs$ls$foundRefs")
                        } else {
                            Logging.getLogger(GitInfo::class.java).error("No git refs dir found")
                        }
                        throw GradleException("Cannot find revision for refName $refName")
                    }
                } else {
                    // Detached HEAD state
                    revision = ref
                }
                return GitInfo(revision)
            } catch (e: IOException) {
                throw GradleException("Cannot get git revision", e)
            }
        }

        @Throws(IOException::class)
        private fun readFirstLine(path: Path): String {
            val firstLine: String
            Files.lines(path, StandardCharsets.UTF_8).use { lines ->
                firstLine = lines.findFirst().orElseThrow {
                    IOException("file [$path] is empty")
                }
            }
            return firstLine
        }
    }
}
