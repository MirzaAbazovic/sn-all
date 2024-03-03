--
-- Script mit create/grant Befehlen, um den DB-User
-- 'REPORTUSER' anzulegen.
-- (Datenbankbenutzer mit Berechtigungen fuer die Generierung von
-- Reports in der DB HURRICAN.)
--

CREATE USER "REPORTUSER" PROFILE "DEFAULT" IDENTIFIED BY "troper" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "REPORTUSER";
GRANT "R_REPORT_USER" TO "REPORTUSER";
commit;


