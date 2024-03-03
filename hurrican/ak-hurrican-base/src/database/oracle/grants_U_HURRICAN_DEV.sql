--
-- Script mit create/grant Befehlen, um den DB-User
-- 'HURRICAN_DEV' anzulegen.
-- (Unter diesem Benutzer werden die 'Hurrican'-Tabellen angelegt.)
--

-- Benutzer HURRICAN_DEV anlegen, unter dem die Hurrican-Tables angelegt werden
--   --> kein Applikationsbenutzer!
CREATE USER "HURRICAN_DEV" PROFILE "DEFAULT" IDENTIFIED BY "1nacirruh" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "HURRICAN_DEV";
grant create session, grant any privilege to HURRICAN_DEV;
grant all privileges to HURRICAN_DEV;
commit;


