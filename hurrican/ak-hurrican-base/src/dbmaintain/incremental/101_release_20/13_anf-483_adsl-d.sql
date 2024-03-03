update T_PRODUKT set PROD_NAME_PATTERN='M-net Partner L2TP {REALVARIANTE} {DOWNSTREAM} VPN' where PROD_ID=441;

-- 40 Upstream
Insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
    STR_VALUE, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD,
    SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON,
    GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH, AUTO_EXPIRE)
 Values
   (74, '40.000 kbit/s', 10074, 'UPSTREAM', 40000,
    '40.000', '40.000', 'Upstream 40.000 kbit/s', '0', '0',
    '0', '1', '0', '1', TO_DATE('01/08/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
    TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0', '0');



-- 25/5
Insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
    DISPO, EWSD, SDH, IPS, SCT,
    SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
    AUTO_EXPIRE)
 Values
   (457, 'ADSL-D 25/5', 10247, 'KOMBI', '.',
    '0', '0', '0', '0', '0',
    '1', TO_DATE('01/08/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0',
    '0');

Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 457, null, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 456, 457, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 23, 457, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 27, 457, '1', 0);





-- 50/10
Insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
    DISPO, EWSD, SDH, IPS, SCT,
    SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
    AUTO_EXPIRE)
 Values
   (458, 'ADSL-D 50/10', 10248, 'KOMBI', '.',
    '0', '0', '0', '0', '0',
    '1', TO_DATE('01/08/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0',
    '0');

Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 458, null, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 456, 458, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 24, 458, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 28, 458, '1', 0);





-- 100/40
Insert into T_TECH_LEISTUNG
   (ID, NAME, EXTERN_LEISTUNG__NO, TYP, PROD_NAME_STR,
    DISPO, EWSD, SDH, IPS, SCT,
    SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, VERSION, PREVENT_AUTO_DISPATCH,
    AUTO_EXPIRE)
 Values
   (459, 'ADSL-D 100/40', 10249, 'KOMBI', '.',
    '0', '0', '0', '0', '0',
    '1', TO_DATE('01/08/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, '0',
    '0');

Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 459, null, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 456, 459, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 25, 459, '1', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 441, 74, 459, '1', 0);


