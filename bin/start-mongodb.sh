#!/bin/bash
DK_HOME=$(cd "$(dirname "$0")"; pwd)
CONFDIR="${DK_HOME}/../conf"
LOG_HOME="${DK_HOME}/../logs"
CLASSPATH="${DK_HOME}/../lib/*:${CONFDIR}"
MAINCLASS="com.dubboclub.dk.server.Main"

if [ -z $JAVA_HOME ]; then
	JAVA=java
else
	JAVA="$JAVA_HOME/bin/java"
fi

DUBBO_PROPERTIES="dubbo-mongodb.properties"

"$JAVA" "-Dmonitor.log.home=${LOG_HOME}" "-Ddubbo.properties.file=${DUBBO_PROPERTIES}" -cp $CLASSPATH "${MAINCLASS}" start
