ALTER TABLE T_DSLAM_PROFILE ADD (GUELTIG CHAR(1 BYTE) DEFAULT 1);
COMMENT ON COLUMN T_DSLAM_PROFILE.GUELTIG IS 'Gibt an, ob das DSLAM-Profil neu zugeordnet werden darf.';


-- H13 = ADSL2p+
-- H04 = ADSL1
-- Fastpath TECH_LS = 1

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_6144_256_H_D001_ADSL1' , null,
    '6144', '256', 0,
    'H04', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_7168_256_H_D001' , null,
    '7168', '256', 0,
    'H13', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_24000_1400_F_D001' , null,
    '24000', '1400', 1,
    'H13', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_7168_512_H_D001' , null,
    '7168', '512', 0,
    'H13', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_16000_1280_H_D001' , null,
    '16000', '1280', 0,
    'H13', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_6000_512_H_D001_ADSL1' , null,
    '6000', '512', 0,
    'H04', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_6144_256_F_D001_ADSL1' , null,
    '6144', '256', 1,
    'H04', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_12032_1280_H_D001' , null,
    '12032', '1280', 0,
    'H13', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_24000_1400_F_D001_ADSL1' , null,
    '24000', '1400', 1,
    'H04', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_6144_512_H_ADSL1' , null,
    '6144', '512', 0,
    'H04', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_16000_1280_F_D001' , null,
    '16000', '1280', 1,
    'H13', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_7168_512_F_D001' , null,
    '7168', '512', 1,
    'H13', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_8000_1280_H_D001' , null,
    '8000', '1280', 0,
    'H13', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_8000_1280_H_D001' , null,
    '8000', '1280', 0,
    'H13', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_12032_1280_F_D001' , null,
    '12032', '1280', 1,
    'H13', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_8000_1280_F_D001' , null,
    '8000', '1280', 1,
    'H13', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_2000_256_H_D001' , null,
    '2000', '256', 0,
    'H13', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_7168_256_F_D001' , null,
    '7168', '256', 1,
    'H13', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_6144_512_F_D001_ADSL1' , null,
    '6144', '512', 1,
    'H04', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_7168_512_H_D001_ADSL1' , null,
    '7168', '512', 0,
    'H04', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_16000_1280_H_D001_ADSL1' , null,
    '16000', '1280', 0,
    'H04', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_7168_256_H_D001_ADSL1' , null,
    '7168', '256', 0,
    'H04', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_7168_256_F_D001_ADSL1' , null,
    '7168', '256', 1,
    'H04', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MAXI_7168_512_F_D001_ADSL1' , null,
    '7168', '512', 1,
    'H04', null, null,
    1, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_12032_1280_H_D001_ADSL1' , null,
    '12032', '1280', 0,
    'H04', null, null,
    null, 0);

INSERT INTO T_DSLAM_PROFILE (
   ID, NAME, BEMERKUNG,
   DOWNSTREAM, UPSTREAM, FASTPATH,
   UETV, DOWNSTREAM_TECH_LS, UPSTREAM_TECH_LS,
   FASTPATH_TECH_LS, GUELTIG)
VALUES (S_T_DSLAM_PROFILE_0.nextVal, 'MMAX_12032_1280_F_D001_ADSL1' , null,
    '12032', '1280', 1,
    'H04', null, null,
    1, 0);

