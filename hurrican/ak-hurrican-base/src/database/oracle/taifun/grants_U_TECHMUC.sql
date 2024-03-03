--
-- Script mit create/grant Befehlen, um den DB-User
-- 'TECHMUC' in der Taifun-DB anzulegen.
--

-- Benutzer HURRICAN fuer Taifun-DB anlegen

CREATE USER "TECHMUC" PROFILE "DEFAULT" IDENTIFIED BY "taif97tech"
  DEFAULT TABLESPACE "TAIFUN_DATA" TEMPORARY TABLESPACE "TEMP" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "TECHMUC";
GRANT R_MNET_READ_ONLY TO "TECHMUC";
commit;



