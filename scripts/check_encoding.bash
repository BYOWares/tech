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

# List all files with encoding not supported.
# We do not put this script in pre-commit because it is slow on Windows.

source "$(dirname "${BASH_SOURCE[0]}")/lib.git.bash"

git__move_to_root_dir || exit

TEMP_FILE="$(mktemp -p .)"
log__debug "Using $TEMP_FILE for the analysis"

git ls-files | xargs -l file -i | awk -F: '{print $2":"$1;}' | sort -u > "$TEMP_FILE"

function remove_encoding {
    local nb_lines_before
    local delta

    nb_lines_before=$(wc -l < "$1")
    log__info "$2 encoding is accepted"
    sed -i "/$2/d" "$1"
    delta=$((nb_lines_before-$(wc -l < "$1")))
    log__debug "$delta files matched the $2 encoding"
}

remove_encoding "$TEMP_FILE" "charset=us-ascii"
remove_encoding "$TEMP_FILE" "charset=binary"
remove_encoding "$TEMP_FILE" "charset=utf-8"

nb_lines=$(wc -l < "$TEMP_FILE")
if [[ $nb_lines -gt 0 ]] ; then
    log__warn "$nb_lines files were detected with bad encoding"
    cat "$TEMP_FILE"
else
    log__info "No bad encoding found!"
fi

log__debug "Removing $TEMP_FILE ..."
rm "$TEMP_FILE"
