alter table T_WITA_SEND_LIMIT add KOLLOKATIONS_TYP VARCHAR2(10);
alter table T_WITA_SEND_LIMIT add constraint CHK_KOLLOKATIONS_TYP check (KOLLOKATIONS_TYP in ('HVT', 'FTTC_KVZ'));

alter table T_WITA_SEND_LIMIT add ALLOWED CHAR(1);

update T_WITA_SEND_LIMIT set KOLLOKATIONS_TYP='HVT', ALLOWED='1';

alter table T_WITA_SEND_LIMIT drop constraint UQ_WITASENDLIMIT_GF;
drop index UQ_WITASENDLIMIT_GF;
alter table T_WITA_SEND_LIMIT add constraint UQ_WITASENDLIMIT_GF UNIQUE (GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP);

Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (11, 'BEREITSTELLUNG', 'FTTC_KVZ', '0', 0, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (13, 'KUENDIGUNG_KUNDE', 'FTTC_KVZ', '0', 0, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (14, 'LEISTUNGSMERKMAL_AENDERUNG', 'FTTC_KVZ', '0', 0, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (15, 'LEISTUNGS_AENDERUNG', 'FTTC_KVZ', '0', 0, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (16, 'PORTWECHSEL', 'FTTC_KVZ', '0', 0, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (17, 'PRODUKTGRUPPENWECHSEL', 'FTTC_KVZ', '0', 0, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (18, 'PROVIDERWECHSEL', 'FTTC_KVZ', '0', 0, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (19, 'RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG', 'FTTC_KVZ', '0', 0, 0, SYSDATE);
Insert into T_WITA_SEND_LIMIT (ID, GESCHAEFTSFALL_TYP, KOLLOKATIONS_TYP, ALLOWED, SEND_LIMIT, VERSION, DATEW)
 Values (20, 'VERBUNDLEISTUNG', 'FTTC_KVZ', '0', 0, 0, SYSDATE);

alter table T_WITA_SEND_LIMIT modify KOLLOKATIONS_TYP NOT NULL;
alter table T_WITA_SEND_LIMIT modify ALLOWED NOT NULL;

alter table T_WITA_SEND_COUNT add KOLLOKATIONS_TYP VARCHAR2(10) NOT NULL;
