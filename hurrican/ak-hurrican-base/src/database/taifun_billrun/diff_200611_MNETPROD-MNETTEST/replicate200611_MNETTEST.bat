del .\*.txt

c:\oracle\ora92\bin\sqlplus MNETTEST/1TSETTENM@MNETTEST < .\start_exports_200611.sql

mysql -h 10.1.20.12 -u its -pits taifun <.\start_import_200611_MNETTEST.sql