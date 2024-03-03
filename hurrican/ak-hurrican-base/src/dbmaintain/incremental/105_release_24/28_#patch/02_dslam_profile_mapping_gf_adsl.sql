CREATE OR REPLACE PROCEDURE add_dp_mapping_gfadsl_50_10
IS
  BEGIN
    FOR pf IN (SELECT * FROM T_DSLAM_PROFILE dp
    WHERE
        downstream_tech_ls = 24
        and upstream_tech_ls = 28
        and not exists (select * from T_PROD_2_DSLAMPROFILE where prod_id = 542 and dslam_profile_id = dp.id))
    LOOP
        insert into T_PROD_2_DSLAMPROFILE (prod_id, dslam_profile_id) values (542, pf.id);
    END LOOP;
END;
/

call add_dp_mapping_gfadsl_50_10();
