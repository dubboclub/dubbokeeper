#!/bin/bash
mvn -Dmaven.test.skip=true clean package install -P mongodb,tomcat assembly:assembly -U