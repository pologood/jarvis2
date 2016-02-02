#!/usr/bin/env bash
BIN_PATH=$(cd $(dirname $0);pwd)
JARVIS_HOME=`dirname "$BIN_PATH"`
JARVIS_DAO_HOME=${JARVIS_HOME}/jarvis-dao
cd ${JARVIS_DAO_HOME}
rm ${JARVIS_DAO_HOME}/src/main/resources/com/mogujie/jarvis/dao/generate/*.xml
mvn org.mybatis.generator:mybatis-generator-maven-plugin:generate
