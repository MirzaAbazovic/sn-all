--
-- Script mit create/grant Befehlen, um den DB-User
-- 'HURRICANWRITER' anzulegen.
-- (Datenbankbenutzer mit Schreib- und Leseberechtigungen auf der DB HURRICAN.)
--

CREATE USER "HURRICANWRITER" PROFILE "DEFAULT" IDENTIFIED BY "nacirruh" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "HURRICANWRITER";
GRANT "R_HURRICAN_USER" TO "HURRICANWRITER";
GRANT "R_HURRICAN_HW_ADMIN" TO "HURRICANWRITER";
commit;

