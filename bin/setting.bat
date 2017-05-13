SET CONFDIR=%~dp0%..\conf
SET LOG_HOME=%~dp0%..\logs
SET CLASSPATH=%~dp0..\*;%~dp0..\lib\*;%CLASSPATH%;%CONFDIR%
set MAINCLASS=com.dubboclub.dk.server.Main