delete from T_PROD_2_DSLAMPROFILE 
where PROD_ID = 540
and DSLAM_PROFILE_ID in 
    (select pdslam.DSLAM_PROFILE_ID from T_DSLAM_PROFILE dslam join T_PROD_2_DSLAMPROFILE pdslam 
        on (dslam.ID = PDSLAM.DSLAM_PROFILE_ID)
        where (PDSLAM.PROD_ID = 540) 
        and (DSLAM.DOWNSTREAM_TECH_LS = 23));