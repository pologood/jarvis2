#!/usr/bin/env bash

usage="Usage: jarvis-daemon.sh (start|stop|restart) <jarvis-command>"

# if no args specified, show usage
if [ $# -le 1 ]; then
  echo $usage
  exit 1
fi

bin=`dirname "${BASH_SOURCE-$0}"`
bin=`cd "$bin"; pwd`

JARVIS_PREFIX=`dirname "$bin"`

export JARVIS_CONF_DIR="${JARVIS_PREFIX}/conf"
export JARVIS_LIB_DIR="${JARVIS_PREFIX}/lib"
export JARVIS_LOG_DIR="${JARVIS_PREFIX}/logs"

# get arguments
action=$1
shift
command=$1
shift


if [ -f "${JARVIS_CONF_DIR}/jarvis-env.sh" ]; then
    . "${JARVIS_CONF_DIR}/jarvis-env.sh"
fi


if [ -x "${JAVA_HOME}/bin/java" ]; then
    JAVA="${JAVA_HOME}/bin/java"
else
    JAVA=`which java`
fi

if [ ! -x "${JAVA}" ]; then
    echo "Could not find any executable java binary. Please install java in your PATH or set JAVA_HOME"
    exit 1
fi

echo ${JAVA}

if [ "${command}" == "server" ]; then
    starting_secure_dn="true"
fi
