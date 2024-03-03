-- add new column GESCHAEFTSFALL_ID
ALTER TABLE T_WBCI_MELDUNG ADD (GESCHAEFTSFALL_ID NUMBER(19,0));
ALTER TABLE T_WBCI_MELDUNG ADD CONSTRAINT FK_GESCHAEFTSFALL_ID FOREIGN KEY (GESCHAEFTSFALL_ID) REFERENCES T_WBCI_GESCHAEFTSFALL;

ALTER TABLE T_WBCI_MELDUNG DROP COLUMN VORABSTIMMUNGSID;
ALTER TABLE T_WBCI_MELDUNG DROP COLUMN GESCHAEFTSFALLTYP;
ALTER TABLE T_WBCI_MELDUNG DROP COLUMN ABGEBENDEREKP;
ALTER TABLE T_WBCI_MELDUNG DROP COLUMN AUFNEHMENDEREKP;

ALTER TABLE T_WBCI_REQUEST RENAME COLUMN WBCIGESCHAEFTSFALL_ID TO GESCHAEFTSFALL_ID;