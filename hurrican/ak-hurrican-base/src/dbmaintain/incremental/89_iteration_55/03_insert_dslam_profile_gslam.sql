CREATE OR REPLACE PROCEDURE dslam_profiles_for_nglta
AS
  bgTypId          T_HW_BAUGRUPPEN_TYP.ID%TYPE;
  dslam_profile_id T_DSLAM_PROFILE.ID%TYPE;
  BEGIN
    SELECT
      id
    INTO bgTypId
    FROM T_HW_BAUGRUPPEN_TYP
    WHERE name = 'NGLT-A';
    FOR rec IN (SELECT
                  DISTINCT dslam.name,
                  dslam.bemerkung,
                  dslam.downstream,
                  dslam.upstream,
                  dslam.fastpath,
                  dslam.uetv,
                  dslam.downstream_tech_ls,
                  dslam.upstream_tech_ls,
                  dslam.fastpath_tech_ls,
                  dslam.gueltig,
                  dslam.tm_down,
                  dslam.tm_up,
                  dslam.downstream_netto,
                  dslam.upstream_netto,
                  dslam.l2power,
                  dslam.enabled_for_autochange
                FROM T_DSLAM_PROFILE dslam
                  JOIN T_PROD_2_DSLAMPROFILE p2d
                    ON dslam.id = P2D.DSLAM_PROFILE_ID
                  JOIN T_PRODUKT p
                    ON P2D.PROD_ID = p.prod_id
                WHERE p.prod_id IN (511, 512, 513, 540, 541)) LOOP
    SELECT
      S_T_DSLAM_PROFILE_0.nextVal
    INTO dslam_profile_id
    FROM dual;
    INSERT INTO T_DSLAM_PROFILE (ID, NAME, BEMERKUNG, DOWNSTREAM, UPSTREAM, FASTPATH, UETV, DOWNSTREAM_TECH_LS,
                                 UPSTREAM_TECH_LS, FASTPATH_TECH_LS, GUELTIG, VERSION, TM_DOWN, TM_UP, ADSL1FORCE,
                                 BAUGRUPPEN_TYP, DOWNSTREAM_NETTO, UPSTREAM_NETTO, L2POWER, ENABLED_FOR_AUTOCHANGE)
      VALUES (dslam_profile_id, rec.NAME, rec.BEMERKUNG, rec.DOWNSTREAM, rec.UPSTREAM, rec.FASTPATH, rec.UETV,
              rec.DOWNSTREAM_TECH_LS, rec.UPSTREAM_TECH_LS, rec.FASTPATH_TECH_LS, rec.GUELTIG, 0, rec.TM_DOWN,
              rec.TM_UP, 0, bgTypId, rec.DOWNSTREAM_NETTO, rec.UPSTREAM_NETTO, rec.L2POWER, rec.ENABLED_FOR_AUTOCHANGE);
    INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) VALUES (511, dslam_profile_id);
    INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) VALUES (512, dslam_profile_id);
    INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) VALUES (513, dslam_profile_id);
    INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) VALUES (540, dslam_profile_id);
    INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) VALUES (541, dslam_profile_id);
    END LOOP;
  END;
/

CALL dslam_profiles_for_nglta();
DROP PROCEDURE dslam_profiles_for_nglta;
