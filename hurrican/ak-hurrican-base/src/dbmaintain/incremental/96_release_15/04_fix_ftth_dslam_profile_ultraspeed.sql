-- 300000-er dslam profile fuer MDU Baugruppentypen bzw. deren  Produktzuordnungen loeschen
delete from T_PROD_2_DSLAMPROFILE where prod_id in (512, 513, 540)
    and DSLAM_PROFILE_ID in (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_300000_30000_H' and d.BAUGRUPPEN_TYP in (37, 172, 175));
delete from T_DSLAM_PROFILE where NAME='MD_300000_30000_H' and BAUGRUPPEN_TYP in (37, 172, 175);

-- 300000-er dslam profil fuer OLT Baugruppentyp
INSERT INTO T_DSLAM_PROFILE (ID, NAME, DOWNSTREAM, UPSTREAM, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS, GUELTIG, VERSION, ADSL1FORCE, BAUGRUPPEN_TYP, ENABLED_FOR_AUTOCHANGE)
    VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MD_300000_30000_H', '300000', '30000', '0', 66, 67, '1', 0, '0', 53, '0');

-- 300000-er dslam profil mit OLT Baugruppentyp Zuordnung den Produkten zuordnen (S&F Flat, Surf Flat, Premium GF DSL)
INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
    VALUES (512, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_300000_30000_H' and d.BAUGRUPPEN_TYP = 53));
INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
    VALUES (513, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_300000_30000_H' and d.BAUGRUPPEN_TYP = 53));
INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
    VALUES (540, (select d.id from T_DSLAM_PROFILE d where d.NAME='MD_300000_30000_H' and d.BAUGRUPPEN_TYP = 53));