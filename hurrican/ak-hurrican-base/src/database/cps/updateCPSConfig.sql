--
-- SQL-Script, mit den Konfigurationsdaten fuer
-- den CPS-Zugriff.
--

-- DB als Test-DB markieren!
update t_registry set int_value=0 where id=0;

-- EMail-Benachrichtigung fuer CPS-Jobs
insert into T_REGISTRY (ID, NAME, STR_VALUE, DESCRIPTION)
  values (4050, 'CPS_MAIL_4_JOBS', 'cps-transactions@m-net.de',
  'Mail-Adresse(n) (mit ; getrennt) fuer Benachrichtigungen im Bereich CPS.');
insert into T_REGISTRY (ID, NAME, STR_VALUE, DESCRIPTION)
  values (4051, 'CPS_MAIL_CC_4_JOBS', 'GlinkJo@m-net.de',
  'Mail-Adresse(n) (mit ; getrennt) fuer CC-Benachrichtigungen im Bereich CPS.');
commit;


-- WebService Konfig-Tabelle
CREATE TABLE T_WEBSERVICE_CONFIG (
       ID NUMBER(10) NOT NULL
     , NAME VARCHAR2(50) NOT NULL
     , DEST_SYSTEM VARCHAR2(50) NOT NULL
     , WS_URL VARCHAR2(255) NOT NULL
     , WS_SEC_ACTION VARCHAR2(50)
     , WS_SEC_USER VARCHAR2(50)
     , WS_SEC_PASSWORD VARCHAR2(50)
     , APP_SEC_USER VARCHAR2(50)
     , APP_SEC_PASSWORD VARCHAR2(50)
     , DESCRIPTION VARCHAR2(255)
);
COMMENT ON TABLE T_WEBSERVICE_CONFIG IS 'Tabelle enthaelt die Zugangsdaten zu div. WebServices';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.NAME 
	IS 'Name des WebServices zur besseren Identifizierung';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.DEST_SYSTEM 
	IS 'Angabe des Ziel-Systems, auf das der WebService zugreift (z.B. TAIFUN, CPS)';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.WS_URL 
	IS 'URL fuer den WebService Aufruf';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.WS_SEC_ACTION 
	IS 'Art der WebService-Security, z.B. <UsernameToken>';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.WS_SEC_USER 
	IS 'User-Name fuer die WS-SecurementAction <UsernameToken>';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.WS_SEC_PASSWORD 
	IS 'Passwort fuer die WS-SecurementAction <UsernameToken>';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.APP_SEC_USER 
	IS 'User-Name bei Applikationsbezogener Authentifierzung im WebService';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.APP_SEC_PASSWORD 
	IS 'Passwort bei Applikationsbezogener Authentifierzung im WebService';
COMMENT ON COLUMN T_WEBSERVICE_CONFIG.DESCRIPTION 
	IS 'kurze Beschreibung des WebServices';

ALTER TABLE T_WEBSERVICE_CONFIG
  ADD CONSTRAINT PK_T_WEBSERVICE_CONFIG
      PRIMARY KEY (ID);
commit;

grant select on T_WEBSERVICE_CONFIG TO R_HURRICAN_USER;
grant select on T_WEBSERVICE_CONFIG TO R_HURRICAN_READ_ONLY;
commit;


-- WebService Definitionen anlegen
-- fuer TEST
insert into T_WEBSERVICE_CONFIG (ID, NAME, DEST_SYSTEM, WS_URL, WS_SEC_ACTION, WS_SEC_USER, WS_SEC_PASSWORD,
	APP_SEC_USER, APP_SEC_PASSWORD, DESCRIPTION)
	values (1, 'CPS Source Agent ASYNC', 'CPS', 'http://localhost:4567/', null, null, null, null, null, 
	'WebService des CPS SourceAgents; Asynchroner Modus');
insert into T_WEBSERVICE_CONFIG (ID, NAME, DEST_SYSTEM, WS_URL, WS_SEC_ACTION, WS_SEC_USER, WS_SEC_PASSWORD,
	APP_SEC_USER, APP_SEC_PASSWORD, DESCRIPTION)
	values (2, 'CPS Source Agent SYNC', 'CPS', 'http://localhost:4567/', null, null, null, null, null, 
	'WebService des CPS SourceAgents; Synchroner Modus');
commit;

-- fuer PRODUKTIV
insert into T_WEBSERVICE_CONFIG (ID, NAME, DEST_SYSTEM, WS_URL, WS_SEC_ACTION, WS_SEC_USER, WS_SEC_PASSWORD,
	APP_SEC_USER, APP_SEC_PASSWORD, DESCRIPTION)
	values (1, 'CPS Source Agent ASYNC', 'CPS', 'http://mnettertio03:22100/', null, null, null, null, null,
	'WebService des CPS SourceAgents; Asynchroner Modus');
insert into T_WEBSERVICE_CONFIG (ID, NAME, DEST_SYSTEM, WS_URL, WS_SEC_ACTION, WS_SEC_USER, WS_SEC_PASSWORD,
	APP_SEC_USER, APP_SEC_PASSWORD, DESCRIPTION)
	values (2, 'CPS Source Agent SYNC', 'CPS', 'http://mnettertio03:22000/', null, null, null, null, null,
	'WebService des CPS SourceAgents; Synchroner Modus');
commit;
  


