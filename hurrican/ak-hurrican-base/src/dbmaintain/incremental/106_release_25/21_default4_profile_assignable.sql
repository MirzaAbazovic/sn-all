UPDATE T_HW_BAUGRUPPEN_TYP SET PROFILE_ASSIGNABLE = '0' WHERE PROFILE_ASSIGNABLE IS NULL;
ALTER TABLE T_HW_BAUGRUPPEN_TYP MODIFY PROFILE_ASSIGNABLE CHAR(1) DEFAULT '0' NOT NULL;

