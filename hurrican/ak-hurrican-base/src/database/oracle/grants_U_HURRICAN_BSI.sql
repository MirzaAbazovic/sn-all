--
-- Script mit create/grant Befehlen, um den DB-User
-- 'HURRICAN_BSI' anzulegen.
-- (Unter diesem Benutzer werden die 'Hurrican'-Tabellen angelegt.)
--

-- Benutzer HURRICAN_BSI anlegen, unter dem die Hurrican-Tables angelegt werden
--   --> kein Applikationsbenutzer!
CREATE USER "HURRICAN_BSI" PROFILE "DEFAULT" IDENTIFIED BY "1nacirruh" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "HURRICAN_BSI";
grant create session, grant any privilege to HURRICAN_BSI;
grant all privileges to HURRICAN_BSI;
commit;


