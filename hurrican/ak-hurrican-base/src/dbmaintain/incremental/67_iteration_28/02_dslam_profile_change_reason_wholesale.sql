ALTER TABLE T_DSLAM_PROFILE_CHANGE_REASON ADD WHOLESALE CHAR(1 BYTE) DEFAULT '0';

update T_DSLAM_PROFILE_CHANGE_REASON set WHOLESALE = '1' where ID in (1, 6);