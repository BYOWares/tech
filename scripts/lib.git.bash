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
[[ -z ${LIB_GIT+x} ]] && export LIB_GIT= || return 0 # Cannot source it more than once
source "$LIB_DIR/lib.util.bash"

util__check_commands git || exit 1

function git__move_to_root_dir {
    local root
    root="$(git rev-parse --show-toplevel 2>/dev/null)"
    if [[ $? -ne 0 ]] ; then
        log__error "Current directory is not inside a git repository"
        return 1
    fi
    log__info "Moving to git root directory '$root'"
    cd "$root" || return 1
    return 0
}
