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

TEST_DIR="$(dirname "$(readlink -e "${BASH_SOURCE[0]}")")"
LIB_DIR="$(dirname "${TEST_DIR}")"
source "${TEST_DIR}/lib.test.bash"
source "${LIB_DIR}/lib.logging.bash"

test__print_test_header "Testing the logging library" "Requires visual validation (no actual tests)"

function do_test_log {
    export LOG_LEVEL=INFO
    log__info ">>>> Setting LOG_LEVEL to '$1' and logging at all level"
    export LOG_LEVEL="$1"
    log__fatal "FATAL logs"
    log__error "ERROR logs"
    log__warn  "WARN logs"
    log__info  "INFO logs"
    log__debug "DEBUG logs"
    log__trace "TRACE logs"
}

do_test_log TRACE
do_test_log DEBUG
do_test_log INFO
do_test_log WARN
do_test_log ERROR
do_test_log FATAL
