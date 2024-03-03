--
-- Script mit create/grant Befehlen, um den DB-User
-- 'HURRICANREADER' anzulegen.
-- (Datenbankbenutzer mit Leseberechtigungen auf der DB HURRICAN.)
--

CREATE USER "BSICRM" PROFILE "DEFAULT" IDENTIFIED BY "1mrcisb" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "BSICRM";
GRANT "R_HURRICAN_BSI_VIEWS" TO "BSICRM";

commit;

