#!/bin/bash

#
# Copyright BYOWares
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

function check_sourced {
    # This script is expected to be sourced, and only by other libraries.
    # When not sourced, it is impossible to known whether the calling library was sourced or not.
    # Since the verification is performed inside an function
    # FUNCNAME[0] = check_sourced
    # FUNCNAME[1] = source (this file)
    # FUNCNAME[2] = source (the library)
    if [[ ${#FUNCNAME[@]} -lt 3 || "${FUNCNAME[1]}" != "source" || "${FUNCNAME[2]}" != "source" ]]; then
        local last_index
        local invalid_script

        last_index=$((${#FUNCNAME[@]}-1))
        invalid_script="$(basename "${BASH_SOURCE[$last_index]}")"
        1>&2 echo "This script ($invalid_script) should be sourced, but is not. StackTrace:"
        for index in "${!BASH_SOURCE[@]}" ; do
            1>&2 echo "    [$index] ${BASH_SOURCE[$index]}:${FUNCNAME[$index]}"
        done
        echo
        echo "Expected: source PATH_TO_SCRIPT/$invalid_script"
        exit 1 # exit can be used because at least one of them was not sourced
    fi
}
check_sourced
