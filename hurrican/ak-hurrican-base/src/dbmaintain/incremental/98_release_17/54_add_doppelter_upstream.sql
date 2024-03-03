-- Premium Glasfaser-DSL Doppel-Flat 50/5, 50/6 und 150/15 fehlt der Doppelte Upstream
-- Doppelter Upstream fuer 50/5 MGA
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  (SELECT
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     28,
     400,
     '0',
     0
   FROM dual
   WHERE NOT exists(SELECT *
                    FROM T_PROD_2_TECH_LEISTUNG
                    WHERE prod_id = 540 AND tech_ls_id = 28 AND TECH_LS_DEPENDENCY = 400 AND IS_DEFAULT = '0'));

-- Doppelter Upstream fuer 50/5 TK
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  (SELECT
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     28,
     401,
     '0',
     0
   FROM dual
   WHERE NOT exists(SELECT *
                    FROM T_PROD_2_TECH_LEISTUNG
                    WHERE prod_id = 540 AND tech_ls_id = 28 AND TECH_LS_DEPENDENCY = 401 AND IS_DEFAULT = '0'));

-- Doppelter Upstream fuer 50/6 TK
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  (SELECT
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     28,
     485,
     '0',
     0
   FROM dual
   WHERE NOT exists(SELECT *
                    FROM T_PROD_2_TECH_LEISTUNG
                    WHERE prod_id = 540 AND tech_ls_id = 28 AND TECH_LS_DEPENDENCY = 485 AND IS_DEFAULT = '0'));

-- Doppelter Upstream fuer 150/15 MGA
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  (SELECT
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     67,
     417,
     '0',
     0
   FROM dual
   WHERE NOT exists(SELECT *
                    FROM T_PROD_2_TECH_LEISTUNG
                    WHERE prod_id = 540 AND tech_ls_id = 67 AND TECH_LS_DEPENDENCY = 417 AND IS_DEFAULT = '0'));

-- Doppelter Upstream fuer 150/15 TK
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
  (SELECT
     S_T_PROD_2_TECH_LEISTUNG_0.nextVal,
     540,
     67,
     418,
     '0',
     0
   FROM dual
   WHERE NOT exists(SELECT *
                    FROM T_PROD_2_TECH_LEISTUNG
                    WHERE prod_id = 540 AND tech_ls_id = 67 AND TECH_LS_DEPENDENCY = 418 AND IS_DEFAULT = '0'));
