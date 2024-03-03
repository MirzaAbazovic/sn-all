INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
(select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 417, null, '0', 0 from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where prod_id=540 and tech_ls_id=417));

INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
(select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 418, null, '0', 0 from dual where not exists (select * from T_PROD_2_TECH_LEISTUNG where prod_id=540 and tech_ls_id=418));
