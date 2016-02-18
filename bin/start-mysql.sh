#!/bin/bash
DK_HOME=$(pwd)
"$DK_HOME/setting.sh"

if [ "$JAVA_HOME" != "" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi
DUBBO_PROPERTIES=${CONFDIR}/dubbo-mysql.properties
"$JAVA" "-Dmonitor.log.home=${LOG_DIR} -Ddubbo.properties.file=${DUBBO_PROPERTIES}" -cp "${CLASSPATH}" "${MAINCLASS}" start