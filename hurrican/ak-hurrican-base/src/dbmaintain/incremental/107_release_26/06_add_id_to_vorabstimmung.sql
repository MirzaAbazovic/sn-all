ALTER TABLE T_VORABSTIMMUNG ADD VORABSTIMMUNG_ID NUMBER(19)
CONSTRAINT FK_VORABSTIMMUNG_ID REFERENCES T_WBCI_VORABSTIMMUNG_FAX(ID);