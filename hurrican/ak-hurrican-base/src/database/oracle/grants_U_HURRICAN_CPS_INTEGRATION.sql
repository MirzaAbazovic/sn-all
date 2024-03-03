--
-- Script mit create/grant Befehlen, um den DB-User
-- 'HURRICAN_CPS_INTEGRATION' anzulegen.
-- (Unter diesem Benutzer werden die 'Hurrican'-Tabellen angelegt.)
--
-- Diese DB dient dazu, das CPS-System zu integrieren
--

-- Benutzer HURRICAN_CPS anlegen, unter dem die Hurrican-Tables angelegt werden
--   --> kein Applikationsbenutzer!
CREATE USER "HURRICAN_CPS_INTEGRATION" PROFILE "DEFAULT" IDENTIFIED BY "1nacirruh" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "HURRICAN_CPS_INTEGRATION";
grant create session, grant any privilege to HURRICAN_CPS_INTEGRATION;
grant all privileges to HURRICAN_CPS_INTEGRATION;
commit;


