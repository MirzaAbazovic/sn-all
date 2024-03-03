CREATE OR REPLACE PROCEDURE dslam_profiles_for_nglta
AS
  mduBgTypId       T_HW_BAUGRUPPEN_TYP.ID%TYPE;
  gponBgTypId      T_HW_BAUGRUPPEN_TYP.ID%TYPE;
  dslam_profile_id T_DSLAM_PROFILE.ID%TYPE;
  BEGIN
    SELECT id INTO gponBgTypId FROM T_HW_BAUGRUPPEN_TYP WHERE name = 'NGLTA';
    SELECT id INTO mduBgTypId FROM T_HW_BAUGRUPPEN_TYP WHERE name = 'MA5652G_VDSL2';

    FOR rec IN (SELECT * FROM T_DSLAM_PROFILE dslam WHERE dslam.baugruppen_typ = mduBgTypId) 
    LOOP        
        SELECT S_T_DSLAM_PROFILE_0.nextVal INTO dslam_profile_id FROM dual;
        INSERT INTO T_DSLAM_PROFILE (ID, NAME, BEMERKUNG, DOWNSTREAM, UPSTREAM, FASTPATH, UETV, DOWNSTREAM_TECH_LS,
                                     UPSTREAM_TECH_LS, FASTPATH_TECH_LS, GUELTIG, VERSION, TM_DOWN, TM_UP, ADSL1FORCE,
                                     BAUGRUPPEN_TYP, DOWNSTREAM_NETTO, UPSTREAM_NETTO, L2POWER, ENABLED_FOR_AUTOCHANGE)
        VALUES (dslam_profile_id, rec.NAME, rec.BEMERKUNG, rec.DOWNSTREAM, rec.UPSTREAM, rec.FASTPATH, rec.UETV,
                  rec.DOWNSTREAM_TECH_LS, rec.UPSTREAM_TECH_LS, rec.FASTPATH_TECH_LS, rec.GUELTIG, 0, rec.TM_DOWN,
                  rec.TM_UP, 0, gponbgTypId, rec.DOWNSTREAM_NETTO, rec.UPSTREAM_NETTO, rec.L2POWER, rec.ENABLED_FOR_AUTOCHANGE);

	    FOR mapping IN (SELECT * FROM T_PROD_2_DSLAMPROFILE WHERE dslam_profile_id = rec.id) 
	    LOOP        
            INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID) VALUES (mapping.prod_id, dslam_profile_id);
	    END LOOP;
              
    END LOOP;
  END;
/

-- alte Konfig entfernen
DELETE FROM t_prod_2_dslamprofile
      WHERE dslam_profile_id IN
               (SELECT id
                  FROM t_dslam_profile
                 WHERE BAUGRUPPEN_TYP = (SELECT id
                                           FROM t_hw_baugruppen_typ
                                          WHERE name = 'NGLTA'));

DELETE FROM t_dslam_profile
      WHERE BAUGRUPPEN_TYP = (SELECT id
                                FROM t_hw_baugruppen_typ
                               WHERE name = 'NGLTA');

-- neue Konfig anlegen
CALL dslam_profiles_for_nglta();
DROP PROCEDURE dslam_profiles_for_nglta;
