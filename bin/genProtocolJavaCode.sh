#!/usr/bin/env bash

bin=$(cd $(dirname ${BASH_SOURCE:-$0});pwd)
JARVIS_HOME=`dirname "$bin"`
JARVIS_PROTOCOL_MAIN="${JARVIS_HOME}/jarvis-protocol/src/main"

protoc --java_out=${JARVIS_PROTOCOL_MAIN}/java --proto_path=${JARVIS_PROTOCOL_MAIN}/resources/protos ${JARVIS_PROTOCOL_MAIN}/resources/protos/*.proto
