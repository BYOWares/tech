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

: "${LIB_DIR:=$(dirname "${BASH_SOURCE[0]}")}"
source "$LIB_DIR/lib.source.bash"
[[ -z ${LIB_UTIL+x} ]] && export LIB_UTIL= || return 0 # Cannot source it more than once
source "$LIB_DIR/lib.logging.bash"

function util__arg_count {
    if [[ $1 -ne $(($# - 1)) ]] ; then
        log__error "Expected $1 arguments but received $(($#-1)) (${*:2})"
        return 1
    fi
    return 0
}

# From man command
# Write a string to standard output that indicates the pathname or command that will be used by the shell, in the
# current shell execution environment, to invoke command_name, but do not invoke command_name.

# command -v "echo hello" >> result in error
# command -v echo hello   >> result in success (print echo)
# This function is not expected to check that a given argument works fine ...
function util__check_commands {
    for c in "$@" ; do
        if ! command -v "$c" >/dev/null ; then # ... Hence the double quote around $c in this line
            log__error "Command '$c' does not exist, script cannot go further"
            return 1
        fi
    done
    return 0
}
