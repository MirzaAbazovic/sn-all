ALTER TABLE T_IA_LEVEL3 DROP CONSTRAINT UQ_IA_LEVEL3;
ALTER TABLE T_IA_LEVEL3 ADD CONSTRAINT  UQ_IA_LEVEL3_NAME UNIQUE (LEVEL1_ID, NAME);

ALTER TABLE T_IA_LEVEL5 DROP CONSTRAINT UQ_IA_LEVEL5;
ALTER TABLE T_IA_LEVEL5 ADD CONSTRAINT  UQ_IA_LEVEL5_NAME UNIQUE (LEVEL3_ID, NAME);