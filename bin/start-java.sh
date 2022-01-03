#!/bin/bash
cd `dirname $0`
DK_HOME="`pwd`/.."
CONFDIR="${DK_HOME}/conf"
LOG_HOME="${DK_HOME}/logs"

BOOT_JAR="`ls ${DK_HOME}/lib/$1`"
JAR_NAME="`basename ${BOOT_JAR}`"
DUBBO_PROPERTIES="$2"

if [ -z $JAVA_HOME ]; then
	JAVA=java
else
	JAVA="$JAVA_HOME/bin/java"
fi

mkdir -p $LOG_HOME

echo "Start $JAR_NAME ..."
$JAVA \
  -Dmonitor.log.home="${LOG_HOME}" \
  -DDUBBO_LOG_HOME="${LOG_HOME}" \
  -Ddubbo.properties.file="${CONFDIR}/${DUBBO_PROPERTIES}" \
  -jar ${BOOT_JAR}
