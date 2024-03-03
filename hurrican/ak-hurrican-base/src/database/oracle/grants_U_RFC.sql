--
-- Script mit create/grant Befehlen, um den DB-User
-- 'RFC_USER' anzulegen.
-- (Datenbankbenutzer mit Leseberechtigungen auf der DB HURRICAN.)
--

CREATE USER "RFC" PROFILE "DEFAULT" IDENTIFIED BY "#89ksaP#" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "RFC";
GRANT "R_HURRICAN_READ_ONLY" TO "RFC";

commit;

