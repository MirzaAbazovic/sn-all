ALTER TABLE T_DSLAM_PROFILE ADD (TM_DOWN NUMBER(10), TM_UP NUMBER(10), L2POWER VARCHAR(40), ADSL1FORCE CHAR(1), BAUGRUPPEN_TYP NUMBER(10));
CREATE INDEX IX_FK_DP_2_BAUGRUPPEN_TYP ON T_DSLAM_PROFILE(BAUGRUPPEN_TYP);
ALTER TABLE T_DSLAM_PROFILE ADD CONSTRAINT FK_DP_2_BAUGRUPPEN_TYP FOREIGN KEY (BAUGRUPPEN_TYP) REFERENCES T_HW_BAUGRUPPEN_TYP(ID);