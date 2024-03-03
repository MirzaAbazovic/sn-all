--
-- Script mit create/grant Befehlen, um den DB-User
-- 'AKOMTOOL' in der Taifun-DB anzulegen.
--

-- Benutzer HURRICAN fuer Taifun-DB anlegen
CREATE USER "AKOMTOOL" PROFILE "DEFAULT" IDENTIFIED BY "taif-tt#1" 
  DEFAULT TABLESPACE "TAIFUN_DATA" TEMPORARY TABLESPACE "TEMP" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "AKOMTOOL";
GRANT R_MNET_READ_ONLY TO "AKOMTOOL";
commit;



