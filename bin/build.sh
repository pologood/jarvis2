#!/usr/bin/env bash


version='jarvis-2.0-bin'

bin=`dirname "${BASH_SOURCE-$0}"`
bin=`cd "$bin"; pwd`

JARVIS_PREFIX=`dirname "$bin"`
JARVIS_DIST_DIR="${JARVIS_PREFIX}/dist"
JARVIS_TARGET_DIR="${JARVIS_DIST_DIR}/${version}"

mvn clean install -DskipTests -f ${JARVIS_PREFIX}/pom.xml
mkdir -p ${JARVIS_TARGET_DIR}/lib
find ./ -name *.jar ! -path "./jarvis-web/*" -exec cp {} ${JARVIS_TARGET_DIR}/lib \;
mkdir -p ${JARVIS_TARGET_DIR}/bin
cp ${JARVIS_PREFIX}/bin/jarvis-* ${JARVIS_TARGET_DIR}/bin/
cp -r ${JARVIS_PREFIX}/conf ${JARVIS_TARGET_DIR}/conf
mkdir ${JARVIS_TARGET_DIR}/logs
cd ${JARVIS_DIST_DIR} && tar zcf ${version}.tar.gz ${version} && rm -rf ${version}
