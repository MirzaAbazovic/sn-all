--
-- Script mit create/grant Befehlen, um den DB-User
-- 'AUTHUSER' anzulegen.
-- (Benutzer, um den Login durchzufuehren und die Rollenberechtigungen
-- auszuwerten.)
--

CREATE USER "AUTHUSER" PROFILE "DEFAULT" IDENTIFIED BY "1RESUHTUA" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "AUTHUSER";
GRANT "R_AUTHENTICATION_USER" TO "AUTHUSER";
commit;
