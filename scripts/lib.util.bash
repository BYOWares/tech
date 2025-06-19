#!/bin/bash

: ${LIB_DIR:=$(dirname ${BASH_SOURCE[0]})}
source "$LIB_DIR/lib.source.bash"
[[ -z ${LIB_UTIL+x} ]] && export LIB_UTIL= || return 0 # Cannot source it more than once
source "$LIB_DIR/lib.logging.bash"

function util__arg_count {
    if [[ $1 -ne $(($# - 1)) ]] ; then
        log__error "Expected $1 arguments but received $(($#-1)) (${@:2})"
        return 1
    fi
    return 0
}

function util__check_commands {
    for c in "$@" ; do
        command -v "$c" >/dev/null
        if [[ $? -ne 0 ]] ; then
            log__error "Command '$c' does not exist, script cannot go further"
            return 1
        fi
    done
}
