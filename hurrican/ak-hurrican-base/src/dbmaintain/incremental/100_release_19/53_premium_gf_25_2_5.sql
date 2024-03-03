-- neue Leistungen für Premium Glasfaser-DSL 25/2,5 mit MGA
-- MGA-Leistung, Downstream 25 Mbit (ID 23), Upstream 2,5 Mbit (ID 27)
INSERT INTO T_TECH_LEISTUNG
(ID, NAME, TYP, EXTERN_LEISTUNG__NO,
 PROD_NAME_STR, DESCRIPTION,
 DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL,
 GUELTIG_VON, GUELTIG_BIS)
VALUES
  (419, 'FTTX_GF_25/2.5_MGA', 'KOMBI', 10220,
   ' ', 'Kombi-Leistung, fuehrt zu: 25 MBit, 2.5 MBit, VOIP_MGA, Endgeraeteport',
   '0', '0', '0', '0', '0', '1',
   TO_DATE('01/06/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'));

-- MGA
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  VALUES(
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     300,
     419,
     '1',
     0
  );

-- EG-Port
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  VALUES(
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     350,
     419,
     '1',
     0
  );

-- Downstream 25000
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  VALUES(
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     23,
     419,
     '1',
     0
  );

-- Upstream 2500
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  VALUES(
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     114,
     419,
     '1',
     0
  );

-- Doppelter Upstream (5000) fuer 25/2,5 MGA
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  (SELECT
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     27,
     419,
     '0',
     0
   FROM dual
   WHERE NOT exists(SELECT *
                    FROM T_PROD_2_TECH_LEISTUNG
                    WHERE prod_id = 540 AND tech_ls_id = 27 AND TECH_LS_DEPENDENCY = 419 AND IS_DEFAULT = '0'));
