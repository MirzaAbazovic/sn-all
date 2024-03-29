-- neue Upstreamleistung 100 Mbit/s
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE,
 STR_VALUE, PROD_NAME_STR, DESCRIPTION, DISPO, EWSD,
 SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON,
 GUELTIG_BIS, VERSION, CHANGED_AT)
VALUES
  (71, '100000 kbit/s', 10071, 'UPSTREAM', 100000,
   '100000', '100000', 'Upstream 100.000 kbit/s', '0', '0',
   '0', '1', '0', '1', TO_DATE('09/01/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
   TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 0, sysdate);

-- 100000 Down / 100000 Up Glasfaser-SDSL(FttB/FttH)
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 25, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 Values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 71, 25, '1', 0);
