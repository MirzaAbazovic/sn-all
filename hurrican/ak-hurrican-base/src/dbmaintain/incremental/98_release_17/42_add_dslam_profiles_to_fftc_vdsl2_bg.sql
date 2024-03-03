-- DSLAM-Profil MD_1000_1000_H zum Baugruppentyp NVLTH zuordnen
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_1000_1000_H', '0', 43, 22,
    '1', 0, '0', '0',
    1000, 1000,
    (select ID from t_hw_baugruppen_typ where name='NVLTH'));

-- neues DSLAM-Profil dem Produkt FTTX Telefon zuordnen
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (511,
   (select dp.ID from t_dslam_profile dp where name='MD_1000_1000_H'
      and baugruppen_typ=(select ID from t_hw_baugruppen_typ where name='NVLTH')));
---------------------------------------------------------------------------------------
-- DSLAM-Profil MD_1000_1000_H zum Baugruppentyp NVLTQ zuordnen
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_1000_1000_H', '0', 43, 22,
    '1', 0, '0', '0',
    1000, 1000,
    (select ID from t_hw_baugruppen_typ where name='NVLTQ'));

-- neues DSLAM-Profil dem Produkt FTTX Telefon zuordnen
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (511,
   (select dp.ID from t_dslam_profile dp where name='MD_1000_1000_H'
      and baugruppen_typ=(select ID from t_hw_baugruppen_typ where name='NVLTQ')));
---------------------------------------------------------------------------------------
-- DSLAM-Profil MD_1000_1000_H zum Baugruppentyp NDLTG zuordnen
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_1000_1000_H', '0', 43, 22,
    '1', 0, '0', '0',
    1000, 1000,
    (select ID from t_hw_baugruppen_typ where name='NDLTG'));

-- neues DSLAM-Profil dem Produkt FTTX Telefon zuordnen
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (511,
   (select dp.ID from t_dslam_profile dp where name='MD_1000_1000_H'
      and baugruppen_typ=(select ID from t_hw_baugruppen_typ where name='NDLTG')));
---------------------------------------------------------------------------------------
-- DSLAM-Profil MD_1000_1000_H zum Baugruppentyp VDBD zuordnen
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_1000_1000_H', '0', 43, 22,
    '1', 0, '0', '0',
    1000, 1000,
    (select ID from t_hw_baugruppen_typ where name='VDBD'));

-- neues DSLAM-Profil dem Produkt FTTX Telefon zuordnen
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (511,
   (select dp.ID from t_dslam_profile dp where name='MD_1000_1000_H'
      and baugruppen_typ=(select ID from t_hw_baugruppen_typ where name='VDBD')));
---------------------------------------------------------------------------------------
-- DSLAM-Profil MD_1000_1000_H zum Baugruppentyp QSTU zuordnen
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_1000_1000_H', '0', 43, 22,
    '1', 0, '0', '0',
    1000, 1000,
    (select ID from t_hw_baugruppen_typ where name='QSTU'));

-- neues DSLAM-Profil dem Produkt FTTX Telefon zuordnen
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (511,
   (select dp.ID from t_dslam_profile dp where name='MD_1000_1000_H'
      and baugruppen_typ=(select ID from t_hw_baugruppen_typ where name='QSTU')));
---------------------------------------------------------------------------------------
-- DSLAM-Profil MD_1000_1000_H zum Baugruppentyp MA5652G_VDSL2 zuordnen
Insert into T_DSLAM_PROFILE
   (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
    GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE,
    DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
 Values
   (S_T_DSLAM_PROFILE_0.nextVal, 'MD_1000_1000_H', '0', 43, 22,
    '1', 0, '0', '0',
    1000, 1000,
    (select ID from t_hw_baugruppen_typ where name='MA5652G_VDSL2'));

-- neues DSLAM-Profil dem Produkt FTTX Telefon zuordnen
Insert into T_PROD_2_DSLAMPROFILE
   (PROD_ID, DSLAM_PROFILE_ID)
 Values
   (511,
   (select dp.ID from t_dslam_profile dp where name='MD_1000_1000_H'
      and baugruppen_typ=(select ID from t_hw_baugruppen_typ where name='MA5652G_VDSL2')));
