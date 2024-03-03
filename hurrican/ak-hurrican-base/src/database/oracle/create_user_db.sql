--
-- SQL- bzw. ORA-Befehle fuer folgende Aktionen:
--   - neuen User mit vollen Berechtigungen anlegen
--   - Export einer Datenbank
--   - Import einer Datenbank in ein anderes Schema (auf einen anderen User)
--

CREATE USER "_USER_NEW_" PROFILE "DEFAULT" IDENTIFIED BY "_PASSWORD_NEW_" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "_USER_NEW_";
grant create session, grant any privilege to _USER_NEW_;
grant all privileges to _USER_NEW_;
commit;


-- Export von HURRICAN@HCPROD01
--   - mit Putty auf mnetdbsvr17.m-net.de verbinden (User: oracle)
--   - Umgebung über ". .oracle_hcprod01" setzen
exp HURRICAN file=/home/oracle/dumps/HURRICAN.dmp log=/home/oracle/dumps/export.log


-- File von dem Produktiv-System auf das Testsystem kopieren:
scp HURRICAN.dmp oracle@mnetdbsvr42.m-net.de:/home/oracle/dumps/HURRICAN.dmp


-- Import einer exportierten Datenbank auf einen anderen User (gleicher Tablespace)
-- (Bei Import auf mnetdbsvr42.intern muessen die Umgebungsvariablen gesetzt werden. Vorgang:
--   - Login mit User oracle
--   - Folgenden Befehl im oracle-home ausfuehren: ". .oracle_hctest01" (oder einfach HCTEST01 eingeben)
--      --> Umgebungsvariablen werden fuer ORA-Instanz HCTEST01 gesetzt
--     (Alternativ zu HCTEST01 kann die Umgebung auch fuer HCDEV01 gesetzt werden.) 
imp system file=/home/oracle/dumps/HURRICAN.dmp log=/home/oracle/dumps/import.log FROMUSER=_USER_OLD_ TOUSER=_USER_NEW_

