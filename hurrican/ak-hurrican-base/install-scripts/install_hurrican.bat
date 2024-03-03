
REM Hurrican II wird aktualisiert
REM Bitte warten...

if not exist C:\Datenbank/nul md C:\Datenbank
rem cacls "C:\Datenbank" /T /E /C /P Benutzer:C
xcopy \\LAN_Server_1\SWInst\Datenbanken\HurricanII\ak-hurrican-gui c:\Datenbank\ak-hurrican-gui /S /E /Q /I /R /Y /D

IF NOT EXIST "%ALLUSERSPROFILE%\Desktop\HurricanII" (
  copy C:\Datenbank\ak-hurrican-gui\HurricanII.lnk "%ALLUSERSPROFILE%\Desktop" /Y
)

IF EXIST C:\Datenbank\ak-hurrican-gui\data\reports\erechnung.mde (
  del C:\Datenbank\ak-hurrican-gui\data\reports\erechnung.mde
)

IF EXIST C:\Datenbank\ak-hurrican-gui\data\reports\migration (
  rmdir /Q /S C:\Datenbank\ak-hurrican-gui\data\reports\migration
)

regedit /s \\LAN_Server_1\SWInst\Datenbanken\HurricanII\hurrican_serienbrief.reg
regedit /s \\LAN_Server_1\SWInst\Datenbanken\HurricanII\hurrican_serienbrief_test.reg
