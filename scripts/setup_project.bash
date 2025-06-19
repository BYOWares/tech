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

# Configure this project

source "$(dirname "${BASH_SOURCE[0]}")/lib.git.bash"
source "$(dirname "${BASH_SOURCE[0]}")/lib.logging.bash"

git__move_to_root_dir || exit

function do_log_eval {
    local c="$*"
    log__info "Running $c ..."
    eval "$c"
    local rc="$?"
    if [[ $rc != 0 ]] ; then
        log__error "Command ending in error (exit code=$rc)"
        exit 1
    fi
}

log__info "Setting up the project ..."
do_log_eval git config --local core.hooksPath .githooks/
log__info "Project successfully set up !"
