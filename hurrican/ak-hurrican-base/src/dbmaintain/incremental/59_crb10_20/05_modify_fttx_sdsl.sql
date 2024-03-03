delete from T_PROD_2_TECH_LEISTUNG ptl 
where ptl.PROD_ID = 541 and ptl.TECH_LS_ID in (299,300,301,302);

-- VOIP_MGA inkludiert Endgeraeteport
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 300, '0', 1);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 350, 300, '1', 0);

-- VOIP_TK inkludiert Endgeraeteport
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 301, '0', 1);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 350, 301, '1', 0);

-- VOIP_PMX inkludiert Endgeraeteport
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 302, '0', 1);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 350, 302, '1', 0);

-- 'weiterer Endgeraeteport' dem Produkt zuordnen
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 541, 351, '0', 1);

delete from t_prod_2_tech_leistung where prod_id=541 and tech_ls_id=350 and tech_ls_dependency is null;
delete from T_TECH_LEISTUNG where ID=299;
