@echo off
SETLOCAL
rem #######################################################
rem
rem Start-Skript fuer den CPS Load Test 
rem
rem Ueber den Parameter %1 kann die URL definiert werden.
rem Parameter %2 definiert die Sleep-Time in Millisekunden.
rem 
rem #######################################################
title=CPS Load Test

IF EXIST .\..\jre\bin\java.exe (
   SET HURRICAN_JAVA_HOME=.\..\jre
   goto classpath
)

IF NOT EXIST %HURRICAN_JAVA_HOME%\bin\java.exe (
   SET HURRICAN_JAVA_HOME=%JAVA_HOME%
   goto classpath
)

IF NOT EXIST %HURRICAN_JAVA_HOME%\bin\java.exe (
   goto nojava
)

:classpath
set CLASSPATH=.\web-test-client.jar

@echo on
set WS_URL=%1
rem ws-url: %WS_URL%

set SLEEP_TIME=%2
rem Sleep-Time: %SLEEP_TIME%

@echo off
%HURRICAN_JAVA_HOME%\bin\java -cp %CLASSPATH% -Xms64m -Xmx128m -Djava.library.path=./3rdparty de.mnet.hurrican.webservice.test.loadtest.LoadTest %WS_URL% %SLEEP_TIME%
goto shutdown


:nojava
@echo on
rem System-Variable HURRICAN_JAVA_HOME ist nicht gesetzt!
pause

:nomodus
@echo on
rem Der Applikations-Modus wurde nicht definiert!
pause

:shutdown
ENDLOCAL
rem exit

:done
