ALTER TABLE T_DSLAM_PROFILE RENAME COLUMN DOWNSTREAM TO DOWNSTREAM_STR;
ALTER TABLE T_DSLAM_PROFILE RENAME COLUMN UPSTREAM TO UPSTREAM_STR;
ALTER TABLE T_DSLAM_PROFILE RENAME COLUMN DOWNSTREAM_NETTO TO DOWNSTREAM_NETTO_STR;
ALTER TABLE T_DSLAM_PROFILE RENAME COLUMN UPSTREAM_NETTO TO UPSTREAM_NETTO_STR;

ALTER TABLE T_DSLAM_PROFILE ADD DOWNSTREAM NUMBER(10, 0);
ALTER TABLE T_DSLAM_PROFILE ADD UPSTREAM NUMBER(10, 0);
ALTER TABLE T_DSLAM_PROFILE ADD DOWNSTREAM_NETTO NUMBER(10, 0);
ALTER TABLE T_DSLAM_PROFILE ADD UPSTREAM_NETTO NUMBER(10, 0);

UPDATE T_DSLAM_PROFILE
SET DOWNSTREAM = DOWNSTREAM_STR;
UPDATE T_DSLAM_PROFILE
SET UPSTREAM = UPSTREAM_STR;
UPDATE T_DSLAM_PROFILE
SET DOWNSTREAM_NETTO = DOWNSTREAM_NETTO_STR;
UPDATE T_DSLAM_PROFILE
SET UPSTREAM_NETTO = UPSTREAM_NETTO_STR;

ALTER TABLE T_DSLAM_PROFILE MODIFY DOWNSTREAM NUMBER(10, 0) NOT NULL;
ALTER TABLE T_DSLAM_PROFILE MODIFY UPSTREAM NUMBER(10, 0) NOT NULL;

ALTER TABLE T_DSLAM_PROFILE DROP COLUMN DOWNSTREAM_STR;
ALTER TABLE T_DSLAM_PROFILE DROP COLUMN UPSTREAM_STR;
ALTER TABLE T_DSLAM_PROFILE DROP COLUMN DOWNSTREAM_NETTO_STR;
ALTER TABLE T_DSLAM_PROFILE DROP COLUMN UPSTREAM_NETTO_STR;

-- Bandbreite von long auf int konvertieren
ALTER TABLE T_PHYSIKTYP RENAME COLUMN MAX_BANDWIDTH_DOWNSTREAM TO MAX_BANDWIDTH_DOWNSTREAM_19;
ALTER TABLE T_PHYSIKTYP ADD MAX_BANDWIDTH_DOWNSTREAM NUMBER(10, 0);
UPDATE T_PHYSIKTYP
SET MAX_BANDWIDTH_DOWNSTREAM = MAX_BANDWIDTH_DOWNSTREAM_19;
ALTER TABLE T_PHYSIKTYP DROP COLUMN MAX_BANDWIDTH_DOWNSTREAM_19;

ALTER TABLE T_PHYSIKTYP RENAME COLUMN MAX_BANDWIDTH_UPSTREAM TO MAX_BANDWIDTH_UPSTREAM_19;
ALTER TABLE T_PHYSIKTYP ADD MAX_BANDWIDTH_UPSTREAM NUMBER(10, 0);
UPDATE T_PHYSIKTYP
SET MAX_BANDWIDTH_UPSTREAM = MAX_BANDWIDTH_UPSTREAM_19;
ALTER TABLE T_PHYSIKTYP DROP COLUMN MAX_BANDWIDTH_UPSTREAM_19;

ALTER TABLE T_HW_BAUGRUPPEN_TYP RENAME COLUMN MAX_BANDWIDTH_DOWNSTREAM TO MAX_BANDWIDTH_DOWNSTREAM_19;
ALTER TABLE T_HW_BAUGRUPPEN_TYP ADD MAX_BANDWIDTH_DOWNSTREAM NUMBER(10, 0);
UPDATE T_HW_BAUGRUPPEN_TYP
SET MAX_BANDWIDTH_DOWNSTREAM = MAX_BANDWIDTH_DOWNSTREAM_19;
ALTER TABLE T_HW_BAUGRUPPEN_TYP DROP COLUMN MAX_BANDWIDTH_DOWNSTREAM_19;

ALTER TABLE T_HW_BAUGRUPPEN_TYP RENAME COLUMN MAX_BANDWIDTH_UPSTREAM TO MAX_BANDWIDTH_UPSTREAM_19;
ALTER TABLE T_HW_BAUGRUPPEN_TYP ADD MAX_BANDWIDTH_UPSTREAM NUMBER(10, 0);
UPDATE T_HW_BAUGRUPPEN_TYP
SET MAX_BANDWIDTH_UPSTREAM = MAX_BANDWIDTH_UPSTREAM_19;
ALTER TABLE T_HW_BAUGRUPPEN_TYP DROP COLUMN MAX_BANDWIDTH_UPSTREAM_19;