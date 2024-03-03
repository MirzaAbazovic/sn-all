-- NVLTH: zusaetzliche Profile mit TM_DOWN/UP
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, TM_DOWN, TM_UP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_50000_H_9dB', '0', 24, 53,
    '1', 0, '0',
    (select t.id from T_HW_BAUGRUPPEN_TYP t where t.NAME='NVLTH'),
    '0',
    50000, 50000, 9, 9);
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, TM_DOWN, TM_UP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_50000_H_12dB', '0', 24, 53,
    '1', 0, '0',
    (select t.id from T_HW_BAUGRUPPEN_TYP t where t.NAME='NVLTH'),
    '0',
    50000, 50000, 12, 12);


-- NVLTQ
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, TM_DOWN, TM_UP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_50000_H', '0', 24, 53,
    '1', 0, '0',
    (select t.id from T_HW_BAUGRUPPEN_TYP t where t.NAME='NVLTQ'),
    '0',
    50000, 50000, NULL, NULL);
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, TM_DOWN, TM_UP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_50000_H_9dB', '0', 24, 53,
    '1', 0, '0',
    (select t.id from T_HW_BAUGRUPPEN_TYP t where t.NAME='NVLTQ'),
    '0',
    50000, 50000, 9, 9);
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, TM_DOWN, TM_UP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_50000_H_12dB', '0', 24, 53,
    '1', 0, '0',
    (select t.id from T_HW_BAUGRUPPEN_TYP t where t.NAME='NVLTQ'),
    '0',
    50000, 50000, 12, 12);



-- NDLTG
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, TM_DOWN, TM_UP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_50000_H', '0', 24, 53,
    '1', 0, '0',
    (select t.id from T_HW_BAUGRUPPEN_TYP t where t.NAME='NDLTG'),
    '0',
    50000, 50000, NULL, NULL);
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, TM_DOWN, TM_UP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_50000_H_9dB', '0', 24, 53,
    '1', 0, '0',
    (select t.id from T_HW_BAUGRUPPEN_TYP t where t.NAME='NDLTG'),
    '0',
    50000, 50000, 9, 9);
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, TM_DOWN, TM_UP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_50000_50000_H_12dB', '0', 24, 53,
    '1', 0, '0',
    (select t.id from T_HW_BAUGRUPPEN_TYP t where t.NAME='NDLTG'),
    '0',
    50000, 50000, 12, 12);


delete from T_PROD_2_DSLAMPROFILE where DSLAM_PROFILE_ID in
  (select d.ID from T_DSLAM_PROFILE d where d.NAME like 'MD_50000_50000_H%' );

insert into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
  select 541, d.ID from T_DSLAM_PROFILE d where d.NAME like 'MD_50000_50000_H%';