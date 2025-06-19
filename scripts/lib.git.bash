#!/bin/bash

: ${LIB_DIR:=$(dirname ${BASH_SOURCE[0]})}
source "$LIB_DIR/lib.source.bash"
[[ -z ${LIB_GIT+x} ]] && export LIB_GIT= || return 0 # Cannot source it more than once
source "$LIB_DIR/lib.util.bash"

util__check_commands git || exit 1

function git__move_to_root_dir {
    local root="$(git rev-parse --show-toplevel 2>/dev/null)"
    if [[ $? -ne 0 ]] ; then
        log__error "Current directory is not inside a git repository"
        return 1
    fi
    log__info "Moving to git root directory '$root'"
    cd "$root"
    return 0
}
