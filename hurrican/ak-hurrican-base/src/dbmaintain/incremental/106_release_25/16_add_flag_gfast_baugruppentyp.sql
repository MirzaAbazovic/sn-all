ALTER TABLE T_HW_BAUGRUPPEN_TYP ADD PROFILE_ASSIGNABLE CHAR(1);

UPDATE T_HW_BAUGRUPPEN_TYP SET PROFILE_ASSIGNABLE = '1' WHERE ID = 1515;
UPDATE T_HW_BAUGRUPPEN_TYP SET PROFILE_ASSIGNABLE = '1' WHERE ID = 1516;