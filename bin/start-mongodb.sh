#!/bin/bash
cd `dirname $0`
DK_HOME=`pwd`
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

nohup "$JAVA" "-Dmonitor.log.home=${LOG_HOME}" "-Ddubbo.properties.file=${DUBBO_PROPERTIES}" -cp $CLASSPATH "${MAINCLASS}" start &

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
