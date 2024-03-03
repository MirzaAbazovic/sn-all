CREATE TABLE T_USER_TASK_2_AUFTRAGDATEN
(
   ID                   NUMBER(19) NOT NULL,
   VERSION              NUMBER(19) NOT NULL,
   AUFTRAGDATENID       NUMBER(19) NOT NULL,
   KUEDDTTASKID         NUMBER(19) NOT NULL,
   PRIMARY KEY (ID)
);

ALTER TABLE T_USER_TASK_2_AUFTRAGDATEN ADD CONSTRAINT FK_UT2AD_2_AUFTRAGDATEN
  FOREIGN KEY (AUFTRAGDATENID) REFERENCES T_AUFTRAG_DATEN(ID);
ALTER TABLE T_USER_TASK_2_AUFTRAGDATEN ADD CONSTRAINT FK_UT2AD_2_USERTASK
  FOREIGN KEY (KUEDDTTASKID) REFERENCES T_USER_TASK(ID);
  
--
-- Berechtigungen zum Lesen und Schreiben auf die Tabelle
--
GRANT SELECT, INSERT, UPDATE ON T_USER_TASK_2_AUFTRAGDATEN TO R_HURRICAN_USER;
GRANT SELECT ON T_USER_TASK_2_AUFTRAGDATEN TO R_HURRICAN_READ_ONLY;

--
-- Generator fuer den Primaerschluessel
--
CREATE SEQUENCE S_T_USER_TASK_2_AUFTRAGDATEN_0
  START WITH 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;

GRANT SELECT ON S_T_USER_TASK_2_AUFTRAGDATEN_0 TO PUBLIC;
