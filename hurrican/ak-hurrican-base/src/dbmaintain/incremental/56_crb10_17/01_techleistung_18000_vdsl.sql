-- 18000/1000 fuer Prod_ID 512
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 21, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 22, 21, '1', 0);

-- 18000/1000 fuer Prod_ID 513
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 21, '0', 0);
Insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 values (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 22, 21, '1', 0);

-- Parameter nicht mehr notwendig - war lediglich fuer 'alte' Produktionsschnittstelle notwendig!
update t_tech_leistung set parameter=NULL where STR_VALUE='Maxi_Deluxe';
update t_tech_leistung set parameter=NULL where STR_VALUE='Maxi_VoIP';
