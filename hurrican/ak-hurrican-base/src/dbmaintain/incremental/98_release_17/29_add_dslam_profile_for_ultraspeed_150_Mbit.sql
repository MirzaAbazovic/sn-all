-- create DSLAM-Profil MD_150000_15000_H for all DPO/ONT Baugruppen with a fitting up- / downstream
CREATE OR REPLACE PROCEDURE create_dslam_profile_15mbit
IS
  BEGIN
    FOR baugruppen_id IN (SELECT ID
                          FROM T_HW_BAUGRUPPEN_TYP
                          WHERE HW_TYPE_NAME IN ('DPO', 'ONT')
                                AND MAX_BANDWIDTH_DOWNSTREAM >= 150000
                                AND MAX_BANDWIDTH_UPSTREAM >= 15000)
    LOOP
      INSERT INTO T_DSLAM_PROFILE
      (ID, NAME, FASTPATH, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
       GUELTIG, VERSION, ADSL1FORCE, ENABLED_FOR_AUTOCHANGE, CHANGED_AT,
       DOWNSTREAM, UPSTREAM, BAUGRUPPEN_TYP)
      VALUES
        (S_T_DSLAM_PROFILE_0.nextVal, 'MD_150000_15000_H', '0', 72, 73,
         '1', 0, '0', '0', sysdate,
         150000, 15000, baugruppen_id.ID);
    END LOOP;
  END;
/
CALL create_dslam_profile_15mbit();
DROP PROCEDURE create_dslam_profile_15mbit;

-- create product mapping for DSLAM-Profil MD_150000_15000_H
CREATE OR REPLACE PROCEDURE create_product_2_dslam_mapping(prodId IN NUMBER)
IS
  BEGIN
    FOR dslam_p IN (SELECT *
                    FROM T_DSLAM_PROFILE
                    WHERE DOWNSTREAM_TECH_LS = 72  -- 150Mbit down
                          AND UPSTREAM_TECH_LS = 73 --15Mbit up
    )
    LOOP
      INSERT INTO T_PROD_2_DSLAMPROFILE
      (PROD_ID, DSLAM_PROFILE_ID)
      VALUES (prodId, dslam_p.ID);
    END LOOP;
  END;
/
-- FFTX_DSL + FON (PROD_ID = 513)
CALL create_product_2_dslam_mapping(513);
-- FFTX_DSL (PROD_ID = 512)
CALL create_product_2_dslam_mapping(512);
-- Premium GF (PROD_ID = 540)
CALL create_product_2_dslam_mapping(540);
DROP PROCEDURE create_product_2_dslam_mapping;