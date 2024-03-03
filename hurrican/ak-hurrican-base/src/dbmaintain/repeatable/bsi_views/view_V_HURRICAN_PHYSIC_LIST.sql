-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die kombinierte Darstellung von Physikdaten
-- (Switch/EQN/DSLAM) je nach Port-Typ.
-- Die Selektion kann ueber den technischen Auftrag oder
-- dem kaufm. Auftrag erfolgen.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_DATEN
--   + T_AUFTRAG_TECHNIK
--   + T_RANGIERUNG
--   + T_EQUIPMENT
--   + T_HW_BAUGRUPPE
--   + T_HW_RACK
--   + T_HW_RACK_DSLAM
--   + T_ENDSTELLE
--   + T_CARRIERBESTELLUNG
--   + T_CARRIER
--   + T_CARRIER_KENNUNG
--   + T_CARRIER_CONTACT
--   + T_HVT_STANDORT
--
-- Besonderheiten:
--   + es wird nur die aktuellste Carrierbestellung beruecksichtigt
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_PHYSIC_LIST
AS SELECT
     ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
     ad.AUFTRAG_ID as TECH_ORDER_ID,
     r.HVT_ID_STANDORT as TECH_LOCATION_ID,
     eq.EQ_ID as PORT_ID,
     eq.HW_EQN as HW_EQN,
     hwr.GERAETEBEZ as DSLAM_NAME,
     (CASE WHEN sw2.NAME IS NOT NULL THEN sw2.NAME ELSE sw.NAME end) AS SWITCH,
     decode(sw.NAME, NULL, '', ''||sw.NAME)
     || decode(hwr.GERAETEBEZ, NULL, '', ' '||hwr.GERAETEBEZ)
     || decode(eq.HW_EQN, NULL, '', ' '||eq.HW_EQN) as PORT_INFO,
     cb.LBZ as LBZ,
     cb.VTRNR as VTRNR,
     cb.CARRIER_ID as CARRIER_ID,
     hs.CARRIER_KENNUNG_ID as CARRIER_KENNUNG_ID,
     hs.CARRIER_CONTACT_ID as CARRIER_CONTACT_ID,
     cm.ID as CARRIER_MAPPING_ID,
     r.ONT_ID as ONT_ID,
     dpu.REVERSE_POWER AS PSE
   FROM
     T_AUFTRAG_DATEN ad
     INNER JOIN T_AUFTRAG_TECHNIK atech on ad.AUFTRAG_ID=atech.AUFTRAG_ID
     INNER JOIN T_ENDSTELLE e on atech.AT_2_ES_ID=e.ES_GRUPPE
     INNER JOIN T_RANGIERUNG r on (e.RANGIER_ID=r.RANGIER_ID or e.RANGIER_ID_ADDITIONAL=r.RANGIER_ID)
     LEFT JOIN T_CARRIERBESTELLUNG cb on e.CB_2_ES_ID=cb.CB_2_ES_ID
     LEFT JOIN T_EQUIPMENT eq on r.EQ_IN_ID=eq.EQ_ID
     LEFT JOIN T_HW_SWITCH sw on (sw.ID = eq.SWITCH)
     LEFT JOIN T_HW_SWITCH sw2 on (sw2.ID = atech.HW_SWITCH)
     LEFT JOIN T_HW_BAUGRUPPE hwbg on eq.HW_BAUGRUPPEN_ID=hwbg.ID
     LEFT JOIN T_HW_RACK hwr ON (hwbg.RACK_ID = hwr.ID AND hwr.RACK_TYP IN ('DSLAM', 'MDU', 'OLT', 'ONT', 'DPO', 'DPU'))
     LEFT JOIN T_HW_RACK_DSLAM hwd on hwr.ID=hwd.RACK_ID
     LEFT JOIN T_HW_RACK_DPU dpu ON hwr.ID = dpu.RACK_ID
     LEFT JOIN T_HVT_STANDORT hs on eq.HVT_ID_STANDORT=hs.HVT_ID_STANDORT
     LEFT JOIN T_CARRIER_KENNUNG ck on hs.CARRIER_KENNUNG_ID=ck.ID
     LEFT JOIN T_CARRIER_CONTACT cc on hs.CARRIER_CONTACT_ID=cc.ID
     LEFT JOIN T_CARRIER_MAPPING cm
       ON (cb.CARRIER_ID=cm.CARRIER_ID
           AND hs.CARRIER_KENNUNG_ID=cm.CARRIER_KENNUNG_ID
           AND hs.CARRIER_CONTACT_ID=cm.CARRIER_CONTACT_ID)
   WHERE
     ad.GUELTIG_BIS > SYSDATE
     AND ad.STATUS_ID < 10000 AND ad.STATUS_ID NOT IN (1150, 3400)
     AND atech.GUELTIG_BIS > SYSDATE
;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_PHYSIC_LIST FOR V_HURRICAN_PHYSIC_LIST;

GRANT SELECT ON V_HURRICAN_PHYSIC_LIST TO R_HURRICAN_BSI_VIEWS;
