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

/**
 * Representation of a version
 *
 * @since XXX
 */
class Version private constructor(
    private val major: Int,
    private val minor: Int,
    private val patch: Int
) : Comparable<Version> {

    override fun compareTo(other: Version): Int {
        if (this.major > other.major) return 1
        if (this.major < other.major) return -1

        if (this.minor > other.minor) return 1
        if (this.minor < other.minor) return -1

        if (this.patch > other.patch) return 1
        if (this.patch < other.patch) return -1

        return 0
    }

    /**
     * Increase the major version by 1 and reset the minor and patch versions to 0.
     *
     * @return the new version
     */
    fun bumpNextMajor(): Version {
        return Version(major + 1, 0, 0)
    }

    /**
     * Increase the minor version by 1, leave the major version untouched and reset the patch version to 0.
     *
     * @return the new version
     */
    fun bumpNextMinor(): Version {
        return Version(major, minor + 1, 0)
    }

    /**
     * Increase the patch version by 1, leave the major and minor versions untouched.
     *
     * @return the new version
     */
    fun bumpNextPatch(): Version {
        return Version(major, minor, patch + 1)
    }

    override fun toString(): String {
        return if (this == UNKNOWN) UNKNOWN_STR else "$major.$minor.$patch"
    }

    companion object {
        /** The version represented by XXX, which means the next version to publish */
        val UNKNOWN = Version(-1, -1, -1)
        const val UNKNOWN_STR = "XXX"

        /**
         * Convert a String to a version It accepts two formats:
         *  * XXX : which designs the not yet known version;
         *  * [0-9]+.[0-9]+.[0-9]+ : for valid versions.
         *
         * @param value the String to convert
         */
        fun parse(value: String): Version {
            if (value == UNKNOWN_STR) return UNKNOWN
            val first: Int = value.indexOf('.')
            val last: Int = value.lastIndexOf('.')
            val major = Integer.parseInt(value, 0, first, 10)
            val minor = Integer.parseInt(value, first + 1, last, 10)
            val patch = Integer.parseInt(value, last + 1, value.length, 10)
            return Version(major, minor, patch)
        }
    }
}
