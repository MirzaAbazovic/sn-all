-- add the ItuCarrierCode to the T_WITA_SEND_LIMIT table
ALTER TABLE T_WITA_SEND_LIMIT ADD (ITU_CARRIER_CODE VARCHAR2(15));
ALTER TABLE T_WITA_SEND_LIMIT MODIFY KOLLOKATIONS_TYP VARCHAR2(10) NULL;
ALTER TABLE T_WITA_SEND_LIMIT ADD CONSTRAINT CK_WITA_SEND_LIMIT_GFT
CHECK (GESCHAEFTSFALL_TYP IN (
    'BEREITSTELLUNG',
    'BESTANDSUEBERSICHT',
    'KUENDIGUNG_KUNDE',
    'KUENDIGUNG_TELEKOM',
    'LEISTUNGSMERKMAL_AENDERUNG',
    'LEISTUNGS_AENDERUNG',
    'PORTWECHSEL',
    'PRODUKTGRUPPENWECHSEL',
    'PROVIDERWECHSEL',
    'RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG',
    'VERBUNDLEISTUNG',
    'UNBEKANNT',
    'VA_KUE_MRN',
    'VA_KUE_ORN',
    'VA_RRNP',
    'VA_UNBEKANNT'
  )) ENABLE NOVALIDATE;

alter table T_WITA_SEND_LIMIT drop constraint UQ_WITASENDLIMIT_GF;
drop index UQ_WITASENDLIMIT_GF;
alter table T_WITA_SEND_LIMIT add constraint UQ_WITASENDLIMIT_GF UNIQUE (GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ITU_CARRIER_CODE);

--DTAG
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (21, 'VA_KUE_MRN', 'DEU.DTAG', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (22, 'VA_KUE_ORN', 'DEU.DTAG', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (23, 'VA_RRNP', 'DEU.DTAG', '1', -1, 0, SYSDATE);
--VODAFONE
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (24, 'VA_KUE_MRN', 'DEU.VFDE', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (25, 'VA_KUE_ORN', 'DEU.VFDE', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (26, 'VA_RRNP', 'DEU.VFDE', '1', -1, 0, SYSDATE);
--1UND1
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (27, 'VA_KUE_MRN', 'DEU.1UND1', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (28, 'VA_KUE_ORN', 'DEU.1UND1', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (29, 'VA_RRNP', 'DEU.1UND1', '1', -1, 0, SYSDATE);
--KABEL_DEUTSCHLAND
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (30, 'VA_KUE_MRN', 'DEU.KDVS', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (31, 'VA_KUE_ORN', 'DEU.KDVS', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (32, 'VA_RRNP', 'DEU.KDVS', '1', -1, 0, SYSDATE);
--TELEFONICA
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (33, 'VA_KUE_MRN', 'DEU.TEFGER', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (34, 'VA_KUE_ORN', 'DEU.TEFGER', '1', -1, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, ITU_CARRIER_CODE, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (35, 'VA_RRNP', 'DEU.TEFGER', '1', -1, 0, SYSDATE);

-- add the ItuCarrierCode to the T_WITA_SEND_COUNT table
ALTER TABLE T_WITA_SEND_COUNT ADD (ITU_CARRIER_CODE VARCHAR2(15));
ALTER TABLE T_WITA_SEND_COUNT MODIFY KOLLOKATIONS_TYP VARCHAR2(10) NULL;
ALTER TABLE T_WITA_SEND_COUNT ADD CONSTRAINT CK_WITA_SEND_COUNT_GFT
CHECK (GESCHAEFTSFALL_TYP IN (
    'BEREITSTELLUNG',
    'BESTANDSUEBERSICHT',
    'KUENDIGUNG_KUNDE',
    'KUENDIGUNG_TELEKOM',
    'LEISTUNGSMERKMAL_AENDERUNG',
    'LEISTUNGS_AENDERUNG',
    'PORTWECHSEL',
    'PRODUKTGRUPPENWECHSEL',
    'PROVIDERWECHSEL',
    'RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG',
    'VERBUNDLEISTUNG',
    'UNBEKANNT',
    'VA_KUE_MRN',
    'VA_KUE_ORN',
    'VA_RRNP',
    'VA_UNBEKANNT'
  )) ENABLE NOVALIDATE;