CREATE OR REPLACE PROCEDURE techls_ip_ds_us_4_dsl (prod_id IN NUMBER, ls_buendel_id IN NUMBER, ls_ds_id IN NUMBER, ls_us_id IN NUMBER)
IS
	BEGIN
			INSERT INTO T_PROD_2_TECH_LEISTUNG
			   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
			 VALUES
			   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prod_id, ls_buendel_id, NULL, '0', 0)
			;
			-- dyn. IPv6
			INSERT INTO T_PROD_2_TECH_LEISTUNG
			   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
			 VALUES
			   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prod_id, 57, ls_buendel_id, '1', 0)
			;
			-- Downstream
			INSERT INTO T_PROD_2_TECH_LEISTUNG
			   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
			 VALUES
			   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prod_id, ls_ds_id, ls_buendel_id, '1', 0)
			;
			-- Upstream
			INSERT INTO T_PROD_2_TECH_LEISTUNG
			   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
			 VALUES
			   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prod_id, ls_us_id, ls_buendel_id, '1', 0)
			;
	END;
/

CREATE OR REPLACE PROCEDURE techls_voip_4_dsl (prod_id IN NUMBER, ls_buendel_id IN NUMBER)
IS
	BEGIN
			-- VOIP
			INSERT INTO T_PROD_2_TECH_LEISTUNG
			   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
			 VALUES
			   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, prod_id, 111, ls_buendel_id, '1', 0)
			;
	END; 
/


-- DSL 18000
call techls_ip_ds_us_4_dsl(480, 480, 21, 22);
call techls_ip_ds_us_4_dsl(512, 480, 21, 22);
call techls_ip_ds_us_4_dsl(513, 480, 21, 22);
call techls_ip_ds_us_4_dsl(514, 480, 21, 22);
call techls_ip_ds_us_4_dsl(515, 480, 21, 22);
call techls_voip_4_dsl(480, 480);
-- DSL 25000
call techls_ip_ds_us_4_dsl(512, 481, 23, 114);
call techls_ip_ds_us_4_dsl(513, 481, 23, 114);
call techls_ip_ds_us_4_dsl(514, 481, 23, 114);
call techls_ip_ds_us_4_dsl(515, 481, 23, 114);
-- DSL 50000
call techls_ip_ds_us_4_dsl(512, 482, 24, 27);
call techls_ip_ds_us_4_dsl(513, 482, 24, 27);
call techls_ip_ds_us_4_dsl(514, 482, 24, 27);
call techls_ip_ds_us_4_dsl(515, 482, 24, 27);
-- DSL 100000
call techls_ip_ds_us_4_dsl(512, 483, 25, 28);
call techls_ip_ds_us_4_dsl(513, 483, 25, 28);

drop procedure techls_ip_ds_us_4_dsl;
drop procedure techls_voip_4_dsl;

-- QOS nur fuer 18000er DSL+VoIP
INSERT INTO T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 480, 50, NULL, '0', 0)
;
-- Always On nur fuer 18000er DSL+VoIP
INSERT INTO T_PROD_2_TECH_LEISTUNG
   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
 VALUES
   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 480, 3, NULL, '0', 0)
;	
-- VOIP ADD. fehlt nur noch fuer 18000er DSL+VoIP
INSERT INTO T_PROD_2_TECH_LEISTUNG
	   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
	 VALUES
	   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 480, 113, NULL, '0', 0)
;

-- dyn. IPv4
INSERT INTO T_PROD_2_TECH_LEISTUNG
	   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
	 VALUES
	   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 480, 56, NULL, '0', 0)
;
INSERT INTO T_PROD_2_TECH_LEISTUNG
	   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
	 VALUES
	   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 512, 56, NULL, '0', 0)
;
INSERT INTO T_PROD_2_TECH_LEISTUNG
	   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
	 VALUES
	   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 513, 56, NULL, '0', 0)
;
INSERT INTO T_PROD_2_TECH_LEISTUNG
	   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
	 VALUES
	   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 514, 56, NULL, '0', 0)
;
INSERT INTO T_PROD_2_TECH_LEISTUNG
	   (ID, PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY, IS_DEFAULT, VERSION)
	 VALUES
	   (S_T_PROD_2_TECH_LEISTUNG_0.nextVal, 515, 56, NULL, '0', 0)
;