#!/bin/bash

# List all files with encoding not supported.
# We do not put this script in pre-commit because it is slow on Windows.

source "$(dirname ${BASH_SOURCE[0]})/lib.git.bash"

git__move_to_root_dir || exit

TEMP_FILE="$(mktemp -p .)"
log__debug "Using $TEMP_FILE for the analysis"

git ls-files | xargs -l file -i | awk -F: '{print $2":"$1;}' | sort -u > "$TEMP_FILE"

function remove_encoding {
    local nblines_before=$(cat "$1" | wc -l)
    log__info "$2 encoding is accepted"
    sed -i "/$2/d" "$1"
    let delta=nblines_before-$(cat "$1" | wc -l)
    log__debug "$delta files matched the $2 encoding"
}

remove_encoding "$TEMP_FILE" "charset=us-ascii"
remove_encoding "$TEMP_FILE" "charset=binary"
remove_encoding "$TEMP_FILE" "charset=utf-8"

nblines=$(cat "$TEMP_FILE" | wc -l)
if [[ $nblines -gt 0 ]] ; then
    log__warn "$nblines files were detected with bad encoding"
    cat "$TEMP_FILE"
else
    log__info "No bad encoding found!"
fi

log__debug "Removing $TEMP_FILE ..."
rm "$TEMP_FILE"
