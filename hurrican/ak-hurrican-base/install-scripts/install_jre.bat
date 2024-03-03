
REM JRE wird aktualisiert
REM Bitte warten...

REM altes JRE loeschen
if exist C:\Datenbank\jre del /F /S /Q C:\Datenbank\jre

if not exist C:\Datenbank/nul md C:\Datenbank
xcopy \\LAN_Server_1\SWInst\Datenbanken\HurricanII\jre C:\Datenbank\jre /S /E /Q /I /Y /D

REM JRE wurde aktualisiert
