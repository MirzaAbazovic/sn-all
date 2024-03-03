--
-- Script mit create/grant Befehlen, um den DB-User
-- 'REPORTADMIN' anzulegen.
-- (Datenbankbenutzer mit Berechtigungen fuer die Administration der
-- Reports in der DB HURRICAN.)
--

CREATE USER "REPORTADMIN" PROFILE "DEFAULT" IDENTIFIED BY "troper" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "REPORTADMIN";
GRANT "R_REPORT_USER" TO "REPORTADMIN";
GRANT "R_REPORT_ADMIN" TO "REPORTADMIN";
commit;

