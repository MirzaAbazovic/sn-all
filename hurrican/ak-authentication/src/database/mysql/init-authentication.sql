--
-- Initilaisierungsscript fuer die Authentication-Datenbank.
--

-- Demo-Daten fuer Tabelle 'DEPARTMENT'
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (1, "SDH", "SDH");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (2, "IPS", "IPS");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (3, "EWSD", "EWSD");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (4, "DISPO", "DISPO");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (5, "SCT", "SCT-Augsburg");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (6, "SCT-K", "SCT-Kempten");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (7, "SCV", "Service-Center Vertrieb");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (8, "PM", "Produktmanagement");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (9, "FiBu", "Finanzbuchhaltung");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (10, "ITS", "IT-Services");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (11, "NP", "Netzplanung");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (12, "VERTRIEB", "Vertrieb");
INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION) VALUES (13, "GF", "Geschäftsführung/Sekretariat");
commit;

-- Applikationsnamen setzen
INSERT INTO APPLICATION (ID, NAME, DESCRIPTION) VALUES (1, "Hurrican", "Hurrican II - CustomerCare System");

-- DBs setzen
INSERT INTO db (ID, NAME, DRIVER, URL, SCHEMA, DESCRIPTION) 
	VALUES (3, 'billing', 'oracle.jdbc.driver.OracleDriver', 'jdbc:oracle:thin:@10.0.0.99:1521:PRODAK', 'PRODAK', 'Definition fuer die Billing-Datenbank.');
INSERT INTO db (ID, NAME, DRIVER, URL, SCHEMA, DESCRIPTION) 
	VALUES (4, 'cc', 'com.mysql.jdbc.Driver', 'jdbc:mysql://10.1.20.12:3306/scv', '', 'Definition fuer die Hurrican-DB.');
INSERT INTO db (ID, NAME, DRIVER, URL, SCHEMA, DESCRIPTION) 
	VALUES (5, 'ip', 'com.mysql.jdbc.Driver', 'jdbc:mysql://10.1.2.5/ip', '', 'Datenbank fuer die IP-Datenbank.');
INSERT INTO db (ID, NAME, DRIVER, URL, SCHEMA, DESCRIPTION) 
	VALUES (6, 'kdportal', 'com.mysql.jdbc.Driver', 'jdbc:mysql://10.200.1.23/kdportal,jdbc:mysql://10.200.1.24/kdportal', '', 'Definition fuer die beiden KDPortal-Datenbanken.');

-- DB-Accounts setzen
INSERT INTO account (ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, DESCRIPTION, DB_ID) 
	VALUES (2, 1, 'billing.default', 'hurrican', 'T+95oGvGFTzWJVt4V+Bw1g==', 'DB-Account fuer die PRODAK', 3);
INSERT INTO account (ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, DESCRIPTION, DB_ID) 
	VALUES (3, 1, 'cc.writer', 'hurrican-writer', 'dyMRupr3P1Nl1OkoxnPWiQ==', '', 4);
INSERT INTO account (ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, DESCRIPTION, DB_ID) 
	VALUES (5, 1, 'cc.reader', 'hurrican-reader', 'dyMRupr3P1Nl1OkoxnPWiQ==', '', 4);
INSERT INTO account (ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, DESCRIPTION, DB_ID) 
	VALUES (6, 1, 'ip.default', 'scvlesen', '4Xm/NabFzbs=', '', 5);
INSERT INTO account (ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, DESCRIPTION, DB_ID) 
	VALUES (7, 1, 'kdportal.default', 'scvlesen', '4Xm/NabFzbs=', '', 6);

