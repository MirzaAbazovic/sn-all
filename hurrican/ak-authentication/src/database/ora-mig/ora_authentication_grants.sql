--
-- GRANT-Scripte fuer die AUTHENTICATION-DB
--

CREATE USER "AUTHENTICATION" PROFILE "DEFAULT" IDENTIFIED BY "its" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "AUTHENTICATION";
GRANT "RESOURCE" TO "AUTHENTICATION";
GRANT ALL PRIVILEGES TO "AUTHENTICATION" WITH ADMIN OPTION;
commit;

-- Rolle fuer die Authentication-User anlegen
CREATE ROLE "R_AUTHENTICATION_USER" NOT IDENTIFIED;
GRANT SELECT ON ACCOUNT TO R_AUTHENTICATION_USER;
GRANT SELECT ON APPLICATION TO R_AUTHENTICATION_USER;
GRANT SELECT, INSERT, UPDATE, DELETE ON COMPBEHAVIOR TO R_AUTHENTICATION_USER;
GRANT SELECT ON DB TO R_AUTHENTICATION_USER;
GRANT SELECT ON DEPARTMENT TO R_AUTHENTICATION_USER;
GRANT SELECT, INSERT, UPDATE, DELETE ON GUICOMPONENT TO R_AUTHENTICATION_USER;
GRANT SELECT, INSERT, UPDATE ON PWHISTORY TO R_AUTHENTICATION_USER;
GRANT SELECT ON ROLE TO R_AUTHENTICATION_USER;
GRANT SELECT ON USERACCOUNT TO R_AUTHENTICATION_USER;
GRANT SELECT ON USERROLE TO R_AUTHENTICATION_USER;
GRANT SELECT, UPDATE ON USERS TO R_AUTHENTICATION_USER;
GRANT SELECT, INSERT, DELETE ON USERSESSION TO R_AUTHENTICATION_USER;
commit;

-- User 'AUTHUSER' anlegen
CREATE USER "AUTHUSER" PROFILE "DEFAULT" IDENTIFIED BY "1RESUHTUA" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "AUTHUSER";
GRANT "R_AUTHENTICATION_USER" TO "AUTHUSER";
commit;