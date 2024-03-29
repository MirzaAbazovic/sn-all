-- move the last meldung details from T_WBCI_GESCHAEFTSFALL to T_WBCI_REQUEST

ALTER TABLE T_WBCI_REQUEST ADD LAST_MELDUNG_TYPE VARCHAR2(255 CHAR);
ALTER TABLE T_WBCI_REQUEST ADD LAST_MELDUNG_DATE TIMESTAMP(6);
ALTER TABLE T_WBCI_REQUEST ADD UPDATED_AT TIMESTAMP(6) DEFAULT SYSDATE NOT NULL;
ALTER TABLE T_WBCI_REQUEST ADD LAST_MELDUNG_CODES VARCHAR2(255 CHAR);

ALTER TABLE T_WBCI_GESCHAEFTSFALL DROP COLUMN MELDUNGS_CODES;
ALTER TABLE T_WBCI_GESCHAEFTSFALL DROP COLUMN LAST_MELDUNG_TYPE;
ALTER TABLE T_WBCI_GESCHAEFTSFALL DROP COLUMN LAST_MELDUNG_DATE;