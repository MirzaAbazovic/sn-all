-- Produktmapping von Glasfaser SDSL korrigieren
update t_produkt_mapping set mapping_group=1045 where prod_id=541;

-- zusaetzliche Leitungsart fuer Bb 5000 kbit/s anlegen
insert into T_LEITUNGSART (ID, NAME)
  values (S_T_LEITUNGSART_0.nextval, '5000 kbit/s');

-- DSLAM-Profile korrigieren: Upstream TechLs war auf die Downstream TechLs konfiguriert
update t_dslam_profile set upstream_tech_ls=27 where name='MD_5000_5000_H';
update t_dslam_profile set upstream_tech_ls=28 where name='MD_10000_10000_H';
update t_dslam_profile set upstream_tech_ls=52 where name='MD_25000_25000_H';
update t_dslam_profile set upstream_tech_ls=53 where name='MD_50000_50000_H';

-- zusaetzliche DSLAM Profile fuer BG-Typ 172 anlegen
Insert into T_DSLAM_PROFILE
   (ID, NAME, BAUGRUPPEN_TYP, DOWNSTREAM, UPSTREAM, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE)
 Values (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_5000_H', 172, '50000', '5000', '0', 24, 27, '1', 0, '0');
Insert into T_DSLAM_PROFILE
   (ID, NAME, DOWNSTREAM, UPSTREAM, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP)
 Values (S_T_DSLAM_PROFILE_0.nextVal, 'MD_25000_2500_H', '25000', '2500', '0', 23, 114, '1', 0, '0', 172);


-- DSLAM Profile mit Baugruppen den Produkten zuordnen
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_2500_H' and d.BAUGRUPPEN_TYP=37));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_2500_H' and d.BAUGRUPPEN_TYP=151));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_2500_H' and d.BAUGRUPPEN_TYP=172));

Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_25000_2500_H' and d.BAUGRUPPEN_TYP=37));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_25000_2500_H' and d.BAUGRUPPEN_TYP=151));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_25000_2500_H' and d.BAUGRUPPEN_TYP=172));

Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_5000_H' and d.BAUGRUPPEN_TYP=37));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_5000_H' and d.BAUGRUPPEN_TYP=151));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_5000_H' and d.BAUGRUPPEN_TYP=172));


Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_50000_H' and d.BAUGRUPPEN_TYP=37));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_50000_H' and d.BAUGRUPPEN_TYP=151));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_50000_50000_H' and d.BAUGRUPPEN_TYP=172));

Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_25000_25000_H' and d.BAUGRUPPEN_TYP=37));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_25000_25000_H' and d.BAUGRUPPEN_TYP=151));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_25000_25000_H' and d.BAUGRUPPEN_TYP=172));

Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_10000_10000_H' and d.BAUGRUPPEN_TYP=37));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_10000_10000_H' and d.BAUGRUPPEN_TYP=151));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_10000_10000_H' and d.BAUGRUPPEN_TYP=172));

Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_5000_5000_H' and d.BAUGRUPPEN_TYP=37));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_5000_5000_H' and d.BAUGRUPPEN_TYP=151));
Insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
 Values (541, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_5000_5000_H' and d.BAUGRUPPEN_TYP=172));

