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
source "${LIB_DIR}/lib.git.bash"
source "${LIB_DIR}/lib.logging.bash"

test__print_test_header "Testing the git library"

GIT_ROOT_DIR="$(dirname "${LIB_DIR}")"
OUTSIDE_GIT_DIR="$(dirname "${GIT_ROOT_DIR}")"

function safe_cd {
    cd "$1" || { log__fatal "Could not move to '$1'" ; exit 1 ; }
}

function test_git__move_to_root_dir {
    local rc

    safe_cd "$3"
    log__debug "Input={To=$2, From=$3}, Expected return code = $1"
    export LOG_LEVEL=FATAL
    git__move_to_root_dir
    rc="$?"
    export LOG_LEVEL="${CURRENT_LOG_LEVEL}"
    if [[ "$rc" -ne "$1" ]] ; then
        log__fatal "Input={To=$2, From=$3}, Expected return code = $1 ... Invalid return code (was '$rc')"
    else
        if [[ "$PWD" != "$2" ]] ; then
            log__fatal "Input={To=$2, From=$3}, Expected return code = $1 ... Invalid destination (was '$PWD')"
        else
            log__info "Input={To=$2, From=$3}, Expected return code = $1 ... Successful"
        fi
    fi
}

test__print_test_case "Testing git__move_to_root_dir"

test_git__move_to_root_dir 0 "${GIT_ROOT_DIR}"    "$(readlink -e "${LIB_DIR}/tests")"
test_git__move_to_root_dir 0 "${GIT_ROOT_DIR}"    "$(readlink -e "${LIB_DIR}/")"
test_git__move_to_root_dir 0 "${GIT_ROOT_DIR}"    "$(readlink -e "${LIB_DIR}/..")"
test_git__move_to_root_dir 1 "${OUTSIDE_GIT_DIR}" "$(readlink -e "${LIB_DIR}/../..")"
