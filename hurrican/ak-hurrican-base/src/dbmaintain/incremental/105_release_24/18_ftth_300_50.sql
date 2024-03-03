update T_PROD_2_TECH_LEISTUNG set tech_ls_id = 53 where prod_id = 600 and tech_ls_id = 67;

delete from T_PROD_2_DSLAMPROFILE where prod_id = 600 and dslam_profile_id
    in (select id from T_DSLAM_PROFILE dp where upstream_tech_ls = 67 and downstream_tech_ls = 66);


CREATE OR REPLACE PROCEDURE create_prod_2_dslam_profile
IS
    BEGIN
     FOR dp IN (select dp.ID from T_DSLAM_PROFILE dp where dp.downstream_tech_ls = 66 and upstream_tech_ls = 53) LOOP
         INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
             VALUES (600, dp.id)
         ;
     END LOOP;
    END;
/

call create_prod_2_dslam_profile();
