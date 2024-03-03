INSERT into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
    VALUES (540, (select id from T_DSLAM_PROFILE where downstream_tech_ls = 25 and upstream_tech_ls = 54 and baugruppen_typ is null));

INSERT into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
    VALUES (540, (select id from T_DSLAM_PROFILE where downstream_tech_ls = 25 and upstream_tech_ls = 54 and baugruppen_typ = 175));

INSERT into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
    VALUES (540, (select id from T_DSLAM_PROFILE where downstream_tech_ls = 25 and upstream_tech_ls = 54 and baugruppen_typ = 172));

INSERT into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
    VALUES (540, (select id from T_DSLAM_PROFILE where downstream_tech_ls = 25 and upstream_tech_ls = 54 and baugruppen_typ = 37));

Insert into T_DSLAM_PROFILE
(ID, NAME, DOWNSTREAM, UPSTREAM, FASTPATH,
 DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE,
 ENABLED_FOR_AUTOCHANGE, CHANGED_AT, BAUGRUPPEN_TYP)
Values
  (S_T_DSLAM_PROFILE_0.nextval, 'MD_300000_60000_H', '300000', '60000', '0',
   66, 68, '1', 0, '0',
   '0', sysdate, 53);

INSERT into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
VALUES (540, (select id from T_DSLAM_PROFILE where downstream_tech_ls = 66 and upstream_tech_ls = 68 and baugruppen_typ = 53));


Insert into T_DSLAM_PROFILE
(ID, NAME, DOWNSTREAM, UPSTREAM, FASTPATH,
 DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE,
 ENABLED_FOR_AUTOCHANGE, CHANGED_AT, BAUGRUPPEN_TYP)
Values
  (S_T_DSLAM_PROFILE_0.nextval, 'MD_300000_60000_H', '300000', '60000', '0',
   66, 68, '1', 0, '0',
   '0', sysdate, null);

INSERT into T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
VALUES (540, (select id from T_DSLAM_PROFILE where downstream_tech_ls = 66 and upstream_tech_ls = 68 and baugruppen_typ is null));
