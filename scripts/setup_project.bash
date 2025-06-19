#!/bin/bash

# Configure this project

source "$(dirname ${BASH_SOURCE[0]})/lib.git.bash"
source "$(dirname ${BASH_SOURCE[0]})/lib.logging.bash"

git__move_to_root_dir || exit

function do_log_eval {
    local c="$@"
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
