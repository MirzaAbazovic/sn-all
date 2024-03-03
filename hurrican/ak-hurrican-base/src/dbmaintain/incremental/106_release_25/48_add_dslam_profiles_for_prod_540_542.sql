CREATE OR REPLACE PROCEDURE insert_dslam_profile(prod in NUMBER, p_name in STRING)
IS
   BEGIN
    FOR profile IN (SELECT p.ID FROM T_DSLAM_PROFILE p WHERE p.NAME = p_name)
    LOOP
      INSERT INTO T_PROD_2_DSLAMPROFILE (PROD_ID, DSLAM_PROFILE_ID)
      SELECT prod, profile.id
        FROM dual WHERE NOT EXISTS (SELECT * FROM T_PROD_2_DSLAMPROFILE WHERE PROD_ID = prod AND DSLAM_PROFILE_ID =  profile.ID);
    END LOOP;
  END;
/

call insert_dslam_profile(540,'MD_100000_40000_H');
call insert_dslam_profile(542,'MD_25000_5000_H');
call insert_dslam_profile(542,'MD_100000_40000_H');
call insert_dslam_profile(542,'MD_300000_60000_H');
drop procedure insert_dslam_profile;
