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
source "${LIB_DIR}/lib.util.bash"

test__print_test_header "Testing the util library"

function test_util__arg_count {
    local rc

    log__debug "Input={Args=[${*:3}], Args Count= $2}, Expected return code = $1"
    export LOG_LEVEL=FATAL
    util__arg_count "$2" "${@:3}"
    rc="$?"
    export LOG_LEVEL="${CURRENT_LOG_LEVEL}"
    if [[ "$rc" -ne "$1" ]] ; then
        log__fatal "Input={Args=[${*:3}], Args Count= $2}, Expected return code = $1 ... Invalid return code (was '$rc')"
    else
        log__info "Input={Args=[${*:3}], Args Count= $2}, Expected return code = $1 ... Successful"
    fi
}

test__print_test_case "Testing util__arg_count"

test_util__arg_count 0 0
test_util__arg_count 0 1 one
test_util__arg_count 0 1 "uno one"
test_util__arg_count 0 2 one two
test_util__arg_count 1 1 one two
test_util__arg_count 1 3 one two

function test_util__check_commands {
    local rc

    export LOG_LEVEL=FATAL
    util__check_commands "${@:2}"
    rc="$?"
    export LOG_LEVEL="${CURRENT_LOG_LEVEL}"
    if [[ "$rc" -ne "$1" ]] ; then
        log__fatal "Tested commands=[${*:2}], Expected return code = $1 ... Invalid return code (was '$rc')"
    else
        log__info "Tested commands=[${*:2}], Expected return code = $1 ... Successful"
    fi
}

test__print_test_case "Testing util__check_commands"

test_util__check_commands 0 echo cat ls
test_util__check_commands 1 supercalifragilisticexpialidocious echo cat ls
test_util__check_commands 1 echo supercalifragilisticexpialidocious cat ls
test_util__check_commands 1 echo cat supercalifragilisticexpialidocious ls
test_util__check_commands 1 echo cat ls supercalifragilisticexpialidocious
test_util__check_commands 1 "echo hello"
