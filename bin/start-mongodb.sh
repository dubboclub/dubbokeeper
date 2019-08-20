#!/bin/bash
BOOT_JAR="dubbokeeper-server-*.jar"
DUBBO_PROPERTIES="monitor-mongodb.properties"

`dirname $0`/start-java.sh $BOOT_JAR $DUBBO_PROPERTIES
