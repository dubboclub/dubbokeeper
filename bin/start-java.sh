#!/bin/bash
cd `dirname $0`
DK_HOME=`pwd`
CONFDIR="${DK_HOME}/../conf"
LOG_HOME="${DK_HOME}/../logs"

BOOT_JAR="`ls ${DK_HOME}/../lib/$1`"
JAR_NAME="`basename ${BOOT_JAR}`"
DUBBO_PROPERTIES="$2"

LOG_FILE="${LOG_HOME}/${JAR_NAME%.*}.out"
MAX_RETRIES=20

if [ -z $JAVA_HOME ]; then
	JAVA=java
else
	JAVA="$JAVA_HOME/bin/java"
fi

echo "Start $JAR_NAME ..."
nohup $JAVA -Dmonitor.log.home="${LOG_HOME}" \
  -Ddubbo.properties.file="${CONFDIR}/${DUBBO_PROPERTIES}" \
  -jar ${BOOT_JAR} > "$LOG_FILE" 2>&1 &

echo "LOG_HOME: $LOG_HOME"
PID=""
CNT=0
while [ -z "$PID" -a $CNT -lt $MAX_RETRIES ]; do
  CNT=$((CNT+1))
  echo -e ".\c"
  sleep 1
  PID="`jps | grep $JAR_NAME | awk '{print $1}'`"
done

if [ -n "$PID" ]; then
  echo "OK! $JAR_NAME STARTED. PID: $PID"
else
  echo "FAILED to start $JAR_NAME. Please check $LOG_FILE"
fi
