#!/bin/bash
cd `dirname $0`
DK_HOME=`pwd`
CONFDIR="${DK_HOME}/../conf"
LOG_HOME="${DK_HOME}/../logs"
CLASSPATH="${CONFDIR}"

BOOT_JAR="`ls ${DK_HOME}/../lib/$1`"
DUBBO_PROPERTIES="$2"

if [ -z $JAVA_HOME ]; then
	JAVA=java
else
	JAVA="$JAVA_HOME/bin/java"
fi

echo "Start `basename ${BOOT_JAR}` ..."
nohup "$JAVA" "-Dmonitor.log.home=${LOG_HOME}" "-Ddubbo.properties.file=${DUBBO_PROPERTIES}" -cp $CLASSPATH -jar "${BOOT_JAR}" &

CNT=0
while [ $CNT -lt 1 ]; do
  echo -e ".\c"
  sleep 1
  CNT=`ps -ef | grep java | grep "$CONFDIR" | awk '{print $2}' | wc -l`
done

echo "OK!"
PIDS=`ps -ef | grep java | grep "$CONFDIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "LOG_HOME: $LOG_HOME"
