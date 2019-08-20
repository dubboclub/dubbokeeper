setlocal
call "%~dp0setting.bat"

SET DUBBO_PROPERTIES=monitor-lucene.properties
echo on
java "-Dmonitor.log.home=%LOG_HOME%" "-Ddubbo.properties.file=%DUBBO_PROPERTIES%" -cp "%CLASSPATH%" "%MAINCLASS%" start
endlocal
