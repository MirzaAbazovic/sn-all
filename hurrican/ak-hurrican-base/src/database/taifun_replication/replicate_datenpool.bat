del .\*.txt

c:\oracle\ora92\bin\sqlplus MNETAK/1KATENM@MNETAK < .\start_exports.sql

mysql -h 10.1.1.18 -u its -pits datenpool <.\start_import.sql
