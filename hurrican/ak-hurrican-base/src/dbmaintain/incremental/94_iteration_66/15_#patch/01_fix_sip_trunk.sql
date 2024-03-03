UPDATE T_PRODUKT_MAPPING SET MAPPING_PART_TYPE='phone_dsl' WHERE MAPPING_GROUP=580 and prod_id = '580';

delete from t_prod_2_tech_leistung where prod_id = 580 and tech_ls_id >= 500 and tech_ls_id <= 522;
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 500, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 501, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 502, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 503, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 504, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 505, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 506, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 507, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 508, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 509, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 510, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 511, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 512, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 513, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 514, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 515, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 516, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 517, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 518, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 519, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 520, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 521, null, 0, 0);
insert into t_prod_2_tech_leistung (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION) values (s_t_prod_2_tech_leistung_0.nextval, 580, 522, null, 0, 0);

-- falsche/nicht existierende abhaengige Leistung mit ID=0
update T_PROD_2_TECH_LEISTUNG set TECH_LS_DEPENDENCY = null where
(PROD_ID=580 AND TECH_LS_ID=470) OR (PROD_ID=541 AND TECH_LS_ID=460) OR (PROD_ID=69 AND TECH_LS_ID=460) OR (PROD_ID=68 AND TECH_LS_ID=460) OR
(PROD_ID=67 AND TECH_LS_ID=460) OR (PROD_ID=66 AND TECH_LS_ID=460) OR (PROD_ID=64 AND TECH_LS_ID=460) OR (PROD_ID=63 AND TECH_LS_ID=460);

-- Leistungsmapping wird nur ausgefuehrt wenn SNAPSHOT_REL='1' (im Java Code true)
update T_TECH_LEISTUNG set SNAPSHOT_REL = '1' where id = 460 or id = 470 or (id >= 500 and id <= 522);

