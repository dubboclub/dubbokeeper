setlocal
call "%~dp0setting.bat"

SET DUBBO_PROPERTIES=%CONFDIR%\dubbo-lucene.properties
echo on
java "-Dmonitor.log.home=%LOG_DIR% -Ddubbo.properties.file=%DUBBO_PROPERTIES%" -cp "%CLASSPATH%" "%MAINCLASS%" start
endlocal
