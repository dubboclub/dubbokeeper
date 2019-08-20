#!/bin/bash
BOOT_JAR="dubbokeeper-ui-*.jar"
DUBBO_PROPERTIES="application-ui.properties"

`dirname $0`/start-java.sh $BOOT_JAR $DUBBO_PROPERTIES
