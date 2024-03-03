--FFM qualification mappings for Endkundenservice HVT
INSERT INTO T_FFM_QUALIFICATION_MAPPING (ID, QUALIFICATION_ID, STANDORT_REF_ID)
VALUES (S_T_FFM_QUAL_MAPPING_0.nextval, (SELECT q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION = 'Endkundenservice HVT'), 11000);

INSERT INTO T_FFM_QUALIFICATION_MAPPING (ID, QUALIFICATION_ID, STANDORT_REF_ID)
VALUES (S_T_FFM_QUAL_MAPPING_0.nextval, (SELECT q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION = 'Endkundenservice HVT'), 11001);

--FFM qualification mappings for Endkundenservice FTTB
INSERT INTO T_FFM_QUALIFICATION_MAPPING (ID, QUALIFICATION_ID, STANDORT_REF_ID)
VALUES (S_T_FFM_QUAL_MAPPING_0.nextval, (SELECT q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION = 'Endkundenservice FTTB'), 11002);

--FFM qualification mappings for Endkundenservice FTTH
INSERT INTO T_FFM_QUALIFICATION_MAPPING (ID, QUALIFICATION_ID, STANDORT_REF_ID)
VALUES (S_T_FFM_QUAL_MAPPING_0.nextval, (SELECT q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION = 'Endkundenservice FTTH'), 11011);

--FFM qualification mappings for Endkundenservice FTTB_H
INSERT INTO T_FFM_QUALIFICATION_MAPPING (ID, QUALIFICATION_ID, STANDORT_REF_ID)
VALUES (S_T_FFM_QUAL_MAPPING_0.nextval, (SELECT q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION = 'Endkundenservice FTTB_H'), 11017);

--FFM qualification mappings for Endkundenservice FTTC
INSERT INTO T_FFM_QUALIFICATION_MAPPING (ID, QUALIFICATION_ID, STANDORT_REF_ID)
VALUES (S_T_FFM_QUAL_MAPPING_0.nextval, (SELECT q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION = 'Endkundenservice FTTC'), 11013);