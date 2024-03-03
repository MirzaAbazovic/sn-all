UPDATE T_CARRIER SET VA_MODUS = 'WBCI', ITU_CARRIER_CODE = 'DEU.SG' WHERE PORTIERUNGSKENNUNG = 'D218'; -- purpur
UPDATE T_CARRIER SET VA_MODUS = 'WBCI', ITU_CARRIER_CODE = 'DEU.SG' WHERE PORTIERUNGSKENNUNG = 'D146'; -- netzquadrat
INSERT INTO T_CARRIER VALUES(S_T_CARRIER_0.nextval, 'umbra', 3, 0, NULL, 'Umbra Networks', SYSDATE, 'kalejami', 0, 0, 'D248', NULL, 0, 'WBCI', 'DEU.SG'); -- umbra

INSERT INTO T_WITA_CONFIG VALUES(S_T_WITA_CONFIG_0.nextval, 'default.wbci.cdm.version.SIPGATE', 1, SYSDATE, 0);

INSERT INTO T_CARRIER_CONTACT(ID, CARRIER_ID, BRANCH_OFFICE, CONTACT_NAME, FAULT_CLEARING_EMAIL, FAULT_CLEARING_PHONE, TIMEST, USERW, VERSION)
VALUES(S_T_CARRIER_CONTACT_0.nextval, (SELECT ID FROM T_CARRIER WHERE PORTIERUNGSKENNUNG = 'D146'), 'WBCI-Kontakt', 'Percy Christensen', 'christensen@sipgate.de', '0211/30203336', SYSDATE, 'kalejami', 0);
INSERT INTO T_CARRIER_CONTACT(ID, CARRIER_ID, BRANCH_OFFICE, CONTACT_NAME, FAULT_CLEARING_EMAIL, FAULT_CLEARING_PHONE, TIMEST, USERW, VERSION)
VALUES(S_T_CARRIER_CONTACT_0.nextval, (SELECT ID FROM T_CARRIER WHERE PORTIERUNGSKENNUNG = 'D146'), 'WBCI-Eskalationskontakt', 'Jan Bükers', 'buekers@sipgate.de', '0211/30203357', SYSDATE, 'kalejami', 0);

INSERT INTO T_CARRIER_CONTACT(ID, CARRIER_ID, BRANCH_OFFICE, CONTACT_NAME, FAULT_CLEARING_EMAIL, FAULT_CLEARING_PHONE, TIMEST, USERW, VERSION)
VALUES(S_T_CARRIER_CONTACT_0.nextval, (SELECT ID FROM T_CARRIER WHERE PORTIERUNGSKENNUNG = 'D218'), 'WBCI-Kontakt', 'Percy Christensen', 'christensen@sipgate.de', '0211/30203336', SYSDATE, 'kalejami', 0);
INSERT INTO T_CARRIER_CONTACT(ID, CARRIER_ID, BRANCH_OFFICE, CONTACT_NAME, FAULT_CLEARING_EMAIL, FAULT_CLEARING_PHONE, TIMEST, USERW, VERSION)
VALUES(S_T_CARRIER_CONTACT_0.nextval, (SELECT ID FROM T_CARRIER WHERE PORTIERUNGSKENNUNG = 'D218'), 'WBCI-Eskalationskontakt', 'Jan Bükers', 'buekers@sipgate.de', '0211/30203357', SYSDATE, 'kalejami', 0);

INSERT INTO T_CARRIER_CONTACT(ID, CARRIER_ID, BRANCH_OFFICE, CONTACT_NAME, FAULT_CLEARING_EMAIL, FAULT_CLEARING_PHONE, TIMEST, USERW, VERSION)
VALUES(S_T_CARRIER_CONTACT_0.nextval, (SELECT ID FROM T_CARRIER WHERE PORTIERUNGSKENNUNG = 'D248'), 'WBCI-Kontakt', 'Percy Christensen', 'christensen@sipgate.de', '0211/30203336', SYSDATE, 'kalejami', 0);
INSERT INTO T_CARRIER_CONTACT(ID, CARRIER_ID, BRANCH_OFFICE, CONTACT_NAME, FAULT_CLEARING_EMAIL, FAULT_CLEARING_PHONE, TIMEST, USERW, VERSION)
VALUES(S_T_CARRIER_CONTACT_0.nextval, (SELECT ID FROM T_CARRIER WHERE PORTIERUNGSKENNUNG = 'D248'), 'WBCI-Eskalationskontakt', 'Jan Bükers', 'buekers@sipgate.de', '0211/30203357', SYSDATE, 'kalejami', 0);