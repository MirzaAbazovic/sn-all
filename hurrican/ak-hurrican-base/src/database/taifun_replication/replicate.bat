del .\*.txt

c:\oracle\ora92\bin\sqlplus MNETAK/1KATENM@MNETAK < .\start_exports.sql

mysql -h 10.1.20.12 -u its -pits taifun <.\start_import.sql
