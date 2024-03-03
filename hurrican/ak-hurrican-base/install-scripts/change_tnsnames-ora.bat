REM TNSNames umbenennen und die neue Version kopieren
REM 
REM change tnsnames.ora "10.0.0.99" "192.168.229.19"
REM change tnsnames.ora "SID = PRODAK" "SID = MNETPROD"
REM ren TNSNAMES.ora tnsnames.ora
REM 

if exist C:\oracle\ora81 (
  copy c:\oracle\ora81\network\ADMIN\tnsnames.ora c:\oracle\ora81\network\ADMIN\tnsnames.ora.mig
  start \\LAN_Server_1\SWInst\Datenbanken\HurricanII\change c:\oracle\ora81\network\ADMIN\tnsnames.ora "10.0.0.99" "192.168.229.19"

  echo >c:\temp\1.vbs wscript.sleep 200
  cscript c:\temp\1.vbs
  del c:\temp\1.vbs   

  start \\LAN_Server_1\SWInst\Datenbanken\HurricanII\change c:\oracle\ora81\network\ADMIN\tnsnames.ora "SID = PRODAK" "SID = MNETPROD"

  echo >c:\temp\1.vbs wscript.sleep 200
  cscript c:\temp\1.vbs
  del c:\temp\1.vbs   

  ren c:\oracle\ora81\network\ADMIN\TNSNAMES.ORA tnsnames.ora
)


if exist C:\oracle\ora92 (
  copy c:\oracle\ora92\network\ADMIN\tnsnames.ora c:\oracle\ora92\network\ADMIN\tnsnames.ora.mig
  start \\LAN_Server_1\SWInst\Datenbanken\HurricanII\change c:\oracle\ora92\network\ADMIN\tnsnames.ora "10.0.0.99" "192.168.229.19"

  echo >c:\temp\1.vbs wscript.sleep 200
  cscript c:\temp\1.vbs
  del c:\temp\1.vbs   

  start \\LAN_Server_1\SWInst\Datenbanken\HurricanII\change c:\oracle\ora92\network\ADMIN\tnsnames.ora "SID = PRODAK" "SID = MNETPROD"

  echo >c:\temp\1.vbs wscript.sleep 200
  cscript c:\temp\1.vbs
  del c:\temp\1.vbs    

  ren c:\oracle\ora92\network\ADMIN\TNSNAMES.ORA tnsnames.ora
)

