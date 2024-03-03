-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der DSLAM-Profile (historisiert)
-- zu einem techn. Auftrag.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_2_DSLAMPROFILE
--   + T_DSLAM_PROFILE
--   + T_DSLAM_PROFILE_CHANGE_REASON
--
-- Besonderheiten:
--   + Sortierung erfolgt ueber T_AUFTRAG_2_DSLAMPROFILE.ID
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_DSLAM_PROFILE
  AS SELECT
    a2d.AUFTRAG_ID as TECH_ORDER_ID,
    a2d.DSLAM_PROFILE_ID as DSLAM_PROFILE_ID,
    dp.NAME as DSLAM_PROFILE,
    dp.DOWNSTREAM as DOWNSTREAM,
    dp.UPSTREAM as UPSTREAM,
    dp.FASTPATH as FASTPATH,
    a2d.CHANGE_REASON_ID as CHANGE_REASON_ID,
    dcr.NAME as CHANGE_REASON,
    a2d.USERW as USERW,
    a2d.BEMERKUNG as CHANGE_COMMENT,
    a2d.GUELTIG_VON as VALID_FROM,
    a2d.GUELTIG_BIS as VALID_TO
  FROM
    T_AUFTRAG_2_DSLAMPROFILE a2d
    INNER JOIN T_DSLAM_PROFILE dp on a2d.DSLAM_PROFILE_ID=dp.ID
    LEFT JOIN T_DSLAM_PROFILE_CHANGE_REASON dcr on a2d.CHANGE_REASON_ID=dcr.ID
  ORDER BY
    a2d.ID;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DSLAM_PROFILE FOR V_HURRICAN_DSLAM_PROFILE;

GRANT SELECT ON V_HURRICAN_DSLAM_PROFILE TO R_HURRICAN_BSI_VIEWS;
