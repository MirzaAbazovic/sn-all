--
-- Script mit create/grant Befehlen, um den DB-User
-- 'STVOICE' anzulegen.
--

CREATE USER "STVOICE" PROFILE "DEFAULT" IDENTIFIED BY "STVOICE" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
ALTER USER STVOICE QUOTA UNLIMITED ON USERS;
GRANT "CONNECT" TO "STVOICE";
GRANT create table to STVOICE;
GRANT resource to STVOICE;

commit;

