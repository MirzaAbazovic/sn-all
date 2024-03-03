-- VOIP_MGA als abhaengige Leistung zu FTTX_GF_150/15_MGA
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
(select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 300, 417, '1', 0 from dual
    where not exists (select * from T_PROD_2_TECH_LEISTUNG where prod_id=540 and tech_ls_id=300 and TECH_LS_DEPENDENCY=417));

-- Endgeraeteport als abhaengige Leistung zu FTTX_GF_150/15_MGA
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
(select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 350, 417, '1', 0 from dual
    where not exists (select * from T_PROD_2_TECH_LEISTUNG where prod_id=540 and tech_ls_id=350 and TECH_LS_DEPENDENCY=417));

-- VOIP_TK als abhaengige Leistung zu FTTX_GF_150/15_TK
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
(select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 301, 418, '1', 0 from dual
    where not exists (select * from T_PROD_2_TECH_LEISTUNG where prod_id=540 and tech_ls_id=301 and TECH_LS_DEPENDENCY=418));

-- Endgeraeteport als abhaengige Leistung zu FTTX_GF_150/15_TK
INSERT INTO T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
(select S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 540, 350, 418, '1', 0 from dual
    where not exists (select * from T_PROD_2_TECH_LEISTUNG where prod_id=540 and tech_ls_id=350 and TECH_LS_DEPENDENCY=418));
