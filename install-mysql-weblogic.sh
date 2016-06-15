#!/bin/bash
mvn -Dmaven.test.skip=true clean package install -P mysql,weblogic assembly:assembly -U