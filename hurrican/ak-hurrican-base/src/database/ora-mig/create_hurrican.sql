--
-- HURRICAN-Benutzer anlegen
--

-- Benutzer HURRICAN anlegen, unter dem die Hurrican-Tables angelegt werden
--   --> kein Applikationsbenutzer!
CREATE USER "HURRICAN" PROFILE "DEFAULT" IDENTIFIED BY "1nacirruh" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "HURRICAN";
grant create session, grant any privilege to HURRICAN;
grant all privileges to HURRICAN;
commit;
