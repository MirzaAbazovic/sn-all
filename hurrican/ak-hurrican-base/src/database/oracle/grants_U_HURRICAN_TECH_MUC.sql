--
-- Script mit create/grant Befehlen, um den DB-User
-- 'HURRICAN_TECH_MUC' anzulegen.
-- (Unter diesem Benutzer werden die 'Hurrican'-Tabellen angelegt.)
--

-- Benutzer HURRICAN_TECH_MUC anlegen, unter dem die Hurrican-Tables angelegt werden
--   --> kein Applikationsbenutzer!

CREATE USER "HURRICAN_TECH_MUC" PROFILE "DEFAULT" IDENTIFIED BY "cum56tech"
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "HURRICAN_TECH_MUC";
GRANT "R_HURRICAN_BSI_VIEWS" TO "HURRICAN_TECH_MUC";
commit;


