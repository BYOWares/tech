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
[[ -z ${LIB_LOGGING+x} ]] && export LIB_LOGGING= || return 0 # Cannot source it more than once

readonly NONE='\x1b[0m'
readonly DARK='\x1b[1m\x1b[41m'
readonly RED='\x1b[31m'
readonly GREEN='\x1b[32m'
readonly YELLOW='\x1b[33m'
readonly BLUE='\x1b[34m'
readonly CYAN='\x1b[36m'
readonly PURPLE='\x1b[35m'

declare -A LEVEL2INT=(["TRACE"]=0 ["DEBUG"]=1 ["INFO"]=2 ["INFO "]=2 ["WARN "]=3 ["WARN"]=3 ["ERROR"]=4 ["FATAL"]=5)

function do_log {
    # If LOG_LEVEL is not configured, we assume INFO. If a bad level is configured, we also assume INFO
    local date
    local filter_level=${LEVEL2INT[${LOG_LEVEL:-INFO}]}
    local level=${LEVEL2INT[$2]}
    date=$(date "+%Y/%m/%d %H:%M:%S.%N%z" | sed -E 's/([0-9]{3})[0-9]{6}/\1/')
    [[ ${level} -ge ${filter_level:-2} ]] && 1>&2 echo -e "${PURPLE}[$PWD]${NONE} ${date} $1[$2]${NONE} ${*:3}"
}

function log__fatal { do_log "${DARK}"   "FATAL" "$@" ; }
function log__error { do_log "${RED}"    "ERROR" "$@" ; }
function log__warn  { do_log "${YELLOW}" "WARN " "$@" ; }
function log__info  { do_log "${GREEN}"  "INFO " "$@" ; }
function log__debug { do_log "${BLUE}"   "DEBUG" "$@" ; }
function log__trace { do_log "${CYAN}"   "TRACE" "$@" ; }
