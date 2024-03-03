
alter session set nls_date_format='yyyy-mm-dd';
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
    DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, PROD_NAME_STR)
    values (36, '768 kbit/s', 10037, 'DOWNSTREAM', 768, '768',
    '0', '0', '1', '0', '0', '1', '2009-01-01', '2200-01-01', '768');

insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
    DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, PROD_NAME_STR)
    values (37, '128 kbit/s', 10038, 'UPSTREAM', 128, '128',
    '0', '0', '1', '0', '0', '1', '2009-01-01', '2200-01-01', '128');


insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
    values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 34, '0');
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
    values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 35, '0');
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
    values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 32, '0');
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
    values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 460, 33, '0');

update T_PRODUKT set PROD_NAME_PATTERN='ConnectDSL SDSL {DOWNSTREAM}' where PROD_ID=460;
