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

fun set(
    k: String,
    v: String
) {
    project.ext.set(k, v)
}

/* Common place to define all versions used in convetion plugins. */
set("agrona", "2.0.0")
set("atlantafx", "2.0.1")
set("controlsfx", "11.2.1")
set("disruptor", "4.0.0")
set("guardian", "1.1.2")
set("ikonli", "12.3.1")
set("jPlatform", "1.11.4")
set("jackson", "2.18.2")
set("junit", "5.11.4")
set("log4j", "2.24.3")
set("opentest4j", "1.3.0")
set("slf4j", "2.0.16")
set("snake", "2.3")
