#!/bin/bash
mvn -Dmaven.test.skip=true clean package install -P mysql,tomcat assembly:assembly -U