#!/bin/bash

: ${LIB_DIR:=$(dirname ${BASH_SOURCE[0]})}
source "$LIB_DIR/lib.source.bash"
[[ -z ${LIB_LOGGING+x} ]] && export LIB_LOGGING= || return 0 # Cannot source it more than once

readonly NONE='\x1b[0m'
readonly RED='\x1b[0;31m'
readonly GREEN='\x1b[0;32m'
readonly YELLOW='\x1b[0;33m'
readonly BLUE='\x1b[0;36m'
readonly PURPLE='\x1b[0;35m'

declare -A LEVEL2INT=(["TRACE"]=0 ["DEBUG"]=1 ["INFO"]=2 ["INFO "]=2 ["WARN "]=3 ["WARN"]=3 ["ERROR"]=4 ["FATAL"]=5)

function do_log {
    # If LOG_LEVEL is not configured, we assume INFO. If a bad level is configured, we also assume INFO
    local filter_level=${LEVEL2INT[${LOG_LEVEL:-INFO}]}
    local level=${LEVEL2INT[$2]}
    [[ ${level} -ge ${filter_level:-2} ]] && 1>&2 echo -e "${PURPLE}[$PWD]${NONE} $(date "+%Y/%m/%d %H:%M:%S.%N%z" | sed -E 's/([0-9]{3})[0-9]{6}/\1/') $1[$2]${NONE} ${@:3}"
}

function log__error { do_log "${RED}"    "ERROR" $@ ; }
function log__warn  { do_log "${YELLOW}" "WARN " $@ ; }
function log__info  { do_log "${GREEN}"  "INFO " $@ ; }
function log__debug { do_log "${BLUE}"   "DEBUG" $@ ; }
