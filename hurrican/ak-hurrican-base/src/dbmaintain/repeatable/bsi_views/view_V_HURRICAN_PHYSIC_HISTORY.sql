-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der Rangierungs-History.
--
-- Verwendete Tabellen:
--   + T_RANGIERUNG
--   + T_PHYSIKTYP
--   + T_EQUIPMENT
--   + T_HW_BAUGRUPPE
--   + T_HW_RACK
--   + T_HW_RACK_DSLAM
--   + T_HVT_STANDORT
--   + T_HVT_GRUPPE
--   + T_HVT_TECHNIK
--
-- Besonderheiten:
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_PHYSIC_HISTORY
  AS
   SELECT r.RANGIER_ID AS RANGIER_ID,
          r.EQ_IN_ID AS EQ_IN_ID,
          r.EQ_OUT_ID AS EQ_OUT_ID,
          r.HVT_ID_STANDORT AS TECH_LOCATION_ID,
          r.GUELTIG_VON AS RANGIER_VALID_FROM,
          r.GUELTIG_BIS AS RANGIER_VALID_TO,
          r.PHYSIK_TYP AS PHYSIC_TYPE_ID,
          pt.NAME AS PHYSIK_TYPE,
          hwbgtyp.NAME AS HW_PORT,
          eq.HW_EQN AS HW_EQN,
          eq.HW_SCHNITTSTELLE AS HW_INTERFACE,
          eq.RANG_VERTEILER AS RANG_VERTEILER,
          eq.RANG_REIHE AS RANG_REIHE,
          eq.RANG_BUCHT AS RANG_BUCHT,
          eq.RANG_LEISTE1 AS RANG_LEISTE1,
          eq.RANG_STIFT1 AS RANG_STIFT1,
          eq.RANG_LEISTE2 AS RANG_LEISTE2,
          eq.RANG_STIFT2 AS RANG_STIFT2,
          eq.RANG_SS_TYPE AS RANG_SS_TYPE,
          eq.UETV AS UETV,
          eq.CARRIER AS CARRIER,
          eq.V5_PORT AS V5_PORT,
          dlu.MG_NAME AS MEDIAGATEWAY,
          hwr.ID AS DSLAM_ID,
          hwr.GERAETEBEZ AS DSLAM_NAME,
          hwd.ERX_STANDORT AS ERX_LOCATION,
          hwd.IP_ADDRESS AS DSLAM_IP,
          hwd.ANS_SLOT_PORT AS DSLAM_SLOT_PORT,
          ht.HERSTELLER AS DSLAM_PRODUCER,
          sw.NAME AS SWITCH,
          dpu.REVERSE_POWER AS PSE,
          CASE WHEN r.EQ_IN_ID = eq.EQ_ID THEN 1 ELSE 0 END IS_EQ_IN,
          CASE WHEN r.EQ_OUT_ID = eq.EQ_ID THEN 1 ELSE 0 END IS_EQ_OUT,
          r.HISTORY_FROM_RID AS HISTORY_FROM_RID,
          r.HISTORY_COUNT AS HISTORY_COUNT
   FROM T_RANGIERUNG r
   LEFT JOIN T_PHYSIKTYP pt ON r.PHYSIK_TYP = pt.ID
   LEFT JOIN T_EQUIPMENT eq ON (r.EQ_OUT_ID = eq.EQ_ID OR r.EQ_IN_ID = eq.EQ_ID)
    left join T_HW_SWITCH sw on (sw.ID = eq.SWITCH)
   LEFT JOIN T_HW_BAUGRUPPE hwbg ON eq.HW_BAUGRUPPEN_ID = hwbg.ID
   LEFT JOIN T_HW_BAUGRUPPEN_TYP hwbgtyp on hwbg.HW_BG_TYP_ID=hwbgtyp.ID
   LEFT JOIN T_HW_RACK hwr ON (hwbg.RACK_ID = hwr.ID AND hwr.RACK_TYP = 'DSLAM')
   LEFT JOIN T_HW_RACK_DPU dpu ON hwr.ID = dpu.RACK_ID
   LEFT JOIN T_HW_RACK_DSLAM hwd ON hwr.ID = hwd.RACK_ID
   LEFT JOIN T_HW_RACK hwrdlu ON (hwbg.RACK_ID=hwrdlu.ID and hwrdlu.RACK_TYP='DLU')
   LEFT JOIN T_HW_RACK_DLU dlu ON hwrdlu.ID=dlu.RACK_ID
   LEFT JOIN T_HVT_STANDORT hs ON eq.HVT_ID_STANDORT = hs.HVT_ID_STANDORT
   LEFT JOIN T_HVT_GRUPPE hg ON hs.HVT_GRUPPE_ID = hg.HVT_GRUPPE_ID
   LEFT JOIN T_HVT_TECHNIK ht ON hwr.HW_PRODUCER = ht.ID;


CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_PHYSIC_HISTORY FOR V_HURRICAN_PHYSIC_HISTORY;

GRANT SELECT ON V_HURRICAN_PHYSIC_HISTORY TO R_HURRICAN_BSI_VIEWS;
