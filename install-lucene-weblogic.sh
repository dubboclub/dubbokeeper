#!/bin/bash
mvn -Dmaven.test.skip=true clean package install -P lucene,weblogic assembly:assembly -U