ALTER TABLE T_WBCI_MELDUNG ADD (BEGRUENDUNG VARCHAR2(255 CHAR), AENDERUNG_ID_REF VARCHAR2(30 CHAR), STORNO_ID_REF VARCHAR2(30 CHAR));

CREATE TABLE T_WBCI_MPOS_ABBM_RUFNUMMER (id NUMBER(19, 0) NOT NULL, version NUMBER(19, 0), MPOS_ABBM_ID NUMBER(19, 0), rufnummer VARCHAR2(30 CHAR), PRIMARY KEY (id));

ALTER TABLE T_WBCI_MPOS_ABBM_RUFNUMMER ADD CONSTRAINT FK_MPOS_ABBM_ID FOREIGN KEY (MPOS_ABBM_ID) REFERENCES T_WBCI_MELDUNG_POSITION;

GRANT SELECT, INSERT, UPDATE ON T_WBCI_MPOS_ABBM_RUFNUMMER TO R_HURRICAN_USER;

GRANT SELECT ON T_WBCI_MPOS_ABBM_RUFNUMMER TO R_HURRICAN_READ_ONLY;

CREATE SEQUENCE S_T_WBCI_MPOS_ABBM_RUFNUMMER_0;

GRANT SELECT ON S_T_WBCI_MPOS_ABBM_RUFNUMMER_0 TO PUBLIC;
