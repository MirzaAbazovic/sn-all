@echo off
title Hurrican-Update -- Fenster nicht schliessen

rem Update von Hurrican durchfuehren. Ablauf:
rem * ist hurrican-update.xml nicht veraendert, so muss nichts aktualisiert werden -> Fertig
rem * versuche hurrican-gui.jar umzubenennen. Gelingt dies nicht, so laeuft hurrican -> Abbruch
rem * hurrican ohne 3rdparty und jre Unterverzeichnisse syncen
rem * 3rdparty ohne Beachtung von Zeitstempel syncen falls noetig
rem * jre nur synchen, falls release-Datei geaendert

Setlocal EnableDelayedExpansion

set APPDIR=c:\datenbank\ak-hurrican-gui
set REMOTEDIR=\\lisa\datenbanken\hurrican\ak-hurrican-gui
set ROBOCOPY=%REMOTEDIR%\..\robocopy

rem Ausgangszustand herstellen, koennte von einen Abbruch noch umbenannt sein
rename %APPDIR%\hurrican-gui.jar.orig hurrican-gui.jar >nul 2>nul

fc %REMOTEDIR%\hurrican-update.xml %APPDIR%\hurrican-update.xml >nul 2>nul
if !ERRORLEVEL! gtr 0 (
	echo Warte auf Beendigung von Hurrican...
	timeout 1
	rename %APPDIR%\hurrican-gui.jar hurrican-gui.jar.orig
	if !ERRORLEVEL! gtr 0 (
		@echo off
		echo.
		echo *****************************
		echo **
		echo ** Das benoetigte Update kann nicht durchgefuehrt werden, da Hurrican noch in Verwendung ist
		echo **
		echo *****************************
		pause
		goto END
	)
	echo Update durchfuehren...
	rename %APPDIR%\hurrican-gui.jar.orig hurrican-gui.jar

    rem remote JRE Verzeichnisname herausfinden (robocopy kann nicht mit wildcard ...)
    for /f "tokens=*" %%J in ('dir /b /s /a:d "%REMOTEDIR%\jre*"') DO (
        set EXCLUDE_JRE=!EXCLUDE_JRE! /xd %%J
    )

	%ROBOCOPY% %REMOTEDIR% %APPDIR% /mir /ndl /njh /njs !EXCLUDE_JRE! /xd %REMOTEDIR%\3rdparty
	%ROBOCOPY% %REMOTEDIR%\3rdparty %APPDIR%\3rdparty /mir /ndl /njh /njs /xc /xn /xo

    rem update jre
    for /f "tokens=*" %%J in ('dir /b /s /a:d "%REMOTEDIR%\jre*"') DO (
	    fc %REMOTEDIR%\%%~nJ\release %APPDIR%\%%~nJ\release >nul 2>nul
	    if !ERRORLEVEL! gtr 0 (
        echo updating JRE %%~nJ
		    %ROBOCOPY% %REMOTEDIR%\%%~nJ %APPDIR%\%%~nJ /mir /ndl /njh /njs
	    )
    )
)

rem pause
echo Neustart von Hurrican
c:
cd %APPDIR%
call ak-hurrican-gui.bat
exit

:END
exit
