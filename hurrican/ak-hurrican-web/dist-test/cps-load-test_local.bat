@echo off
SETLOCAL
rem #######################################################
rem
rem Start-Skript fuer den CPS Load Test 
rem
rem Ueber den Parameter %1 kann die URL definiert werden
rem 
rem #######################################################
title=CPS Load Test

start cps-load-test.bat http://localhost:8080/hurricanweb 20000
exit
