-- KOMBI '100_VOIP_IPv6 testen'
insert into T_TECH_LEISTUNG
   (ID, NAME, TYP, EXTERN_LEISTUNG__NO, PROD_NAME_STR, DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
 values
   (492, '100_VOIP_IPv6 testen', 'KOMBI', 10246, ' ',
    '0', '0', '0', '0', '0', '1',
    TO_DATE('07/29/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
    TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));

-- '100_VOIP_IPv6 testen' fuer FTTX DSL
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 492, '0', 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 57, 492, '1', 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 25, 492, '1', 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 28, 492, '1', 0);

-- '100_VOIP_IPv6 testen' fuer FTTX DSL+Fon
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 492, '0', 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 57, 492, '1', 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 25, 492, '1', 0);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 28, 492, '1', 0);

