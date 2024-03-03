-- DSLAM-Profil MD_1000_1000_H zum Baugruppentyp MA5651_VDSL2 zuordnen
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE, CHANGED_AT,
    DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_1000_1000_H', '0', 43, 22,
    '1', 0, '0', '0', sysdate,
    1000, 1000,
    (select ID from t_hw_baugruppen_typ where name='MA5651_VDSL2'));

-- neues DSLAM-Profil dem Produkt FTTX Telefon zuordnen
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (511,
   (select dp.ID from t_dslam_profile dp where name='MD_1000_1000_H'
      and baugruppen_typ=(select ID from t_hw_baugruppen_typ where name='MA5651_VDSL2')));
