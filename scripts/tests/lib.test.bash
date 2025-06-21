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

LIB_DIR="$(dirname "$(dirname "$(readlink -e "${BASH_SOURCE[0]}")")")"
source "${LIB_DIR}/lib.logging.bash"

# All tests sourcing this file will be able to remember the log level used set up by the user
export CURRENT_LOG_LEVEL="${LOG_LEVEL:-INFO}"

readonly HEADER="+-------------------------------------------------+"
readonly CASE="    +~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+"

function test__print_test_header {
    log__info ""
    log__info "${HEADER}"
    for line in "${@}" ; do
        log__info "$(printf "| %-$((${#HEADER}-4))s |\n" "${line}")"
    done
    log__info "${HEADER}"
    log__info ""
}

function test__print_test_case {
    log__info "${CASE}"
    for line in "${@}" ; do
        log__info "$(printf "    { %-$((${#CASE}-8))s }\n" "${line}")"
    done
    log__info "${CASE}"
}
