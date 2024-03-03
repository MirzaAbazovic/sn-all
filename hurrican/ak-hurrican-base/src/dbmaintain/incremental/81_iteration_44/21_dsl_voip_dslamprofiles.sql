-- DSLAM-Profile wie fuer dsl analog
CREATE OR REPLACE PROCEDURE create_prod_2_dslam_profile
IS
    BEGIN
     FOR dslam_profile_id IN (select P2D.DSLAM_PROFILE_ID from T_PROD_2_DSLAMPROFILE p2d where p2d.PROD_ID = 421) LOOP
         INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
             VALUES (480, dslam_profile_id.DSLAM_PROFILE_ID)
         ;
     END LOOP;
    END;
/

call create_prod_2_dslam_profile(); 

DROP PROCEDURE create_prod_2_dslam_profile;
