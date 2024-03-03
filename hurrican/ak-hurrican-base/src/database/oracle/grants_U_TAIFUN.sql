--
-- Script mit create/grant Befehlen, um den DB-User
-- 'TAIFUN' anzulegen.
-- (Datenbankbenutzer mit Leseberechtigungen auf Views der DB HURRICAN.)
--

CREATE USER "TAIFUN" PROFILE "DEFAULT" IDENTIFIED BY "1nufiat" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "TAIFUN";
GRANT "R_HURRICAN_TAIFUN_VIEWS" TO "TAIFUN";

commit;

