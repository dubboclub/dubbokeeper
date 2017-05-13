#!/bin/bash
mvn -Dmaven.test.skip=true clean package install -P mongodb assembly:assembly -U