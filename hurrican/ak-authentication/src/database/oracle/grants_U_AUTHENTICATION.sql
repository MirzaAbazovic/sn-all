--
-- Script mit create/grant Befehlen, um den DB-User
-- 'AUTHENTICATION' anzulegen.
-- (Unter diesem Benutzer werden die 'Authentication'-Tabellen angelegt.)
--

CREATE USER "AUTHENTICATION" PROFILE "DEFAULT" IDENTIFIED BY "its" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "AUTHENTICATION";
GRANT "RESOURCE" TO "AUTHENTICATION";
GRANT ALL PRIVILEGES TO "AUTHENTICATION" WITH ADMIN OPTION;
commit;

