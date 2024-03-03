-- Erstelle neue Tabelle T_EG_PORT fuer Zuordnung von Ports zu Endgeraetetypen.
CREATE TABLE T_EG_PORT
(
   ID                   NUMBER (19, 0) NOT NULL,
   VERSION              NUMBER (19, 0) DEFAULT 0,
   EG_TYPE_ID           NUMBER (19, 0) DEFAULT NULL,
   NO                   NUMBER (19, 0) NOT NULL,
   NAME                 VARCHAR2 (50) NOT NULL,
   constraint           FK_EG_PORT_EG_TYPE
   foreign key          (EG_TYPE_ID)
   references           T_EG_TYPE(ID),
   PRIMARY KEY          (ID)
);

-- Zugriff auf die Tabelle regeln
GRANT SELECT, INSERT, UPDATE ON T_EG_PORT TO R_HURRICAN_USER;
GRANT SELECT ON T_EG_PORT TO R_HURRICAN_READ_ONLY;

-- Sequenz fuer Primaerschluessel
CREATE SEQUENCE S_T_EG_PORT_0 START WITH 1;
-- Zugriff auf die Sequenz regeln
GRANT SELECT ON S_T_EG_PORT_0 TO PUBLIC;

-- Befuellen der Daten - Default Endgeraetetyp.
INSERT INTO T_EG_PORT (ID, NO, NAME) VALUES (S_T_EG_PORT_0.nextVal, 1, 'Port Eins');
INSERT INTO T_EG_PORT (ID, NO, NAME) VALUES (S_T_EG_PORT_0.nextVal, 2, 'Port Zwei');
INSERT INTO T_EG_PORT (ID, NO, NAME) VALUES (S_T_EG_PORT_0.nextVal, 3, 'Port Drei');
INSERT INTO T_EG_PORT (ID, NO, NAME) VALUES (S_T_EG_PORT_0.nextVal, 4, 'Port Vier');
