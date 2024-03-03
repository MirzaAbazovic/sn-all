-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der Physik-Daten.
-- Als Einstieg dient die Tabelle T_ENDSTELLE.
--
-- Verwendete Tabellen:
--   + T_ENDSTELLE
--   + T_RANGIERUNG
--   + T_EQUIPMENT
--   + T_HVT_STANDORT
--   + T_HVT_GRUPPE
--   + T_HW_BAUGRUPPE
--   + T_HW_RACK
--   + T_HW_RACK_DSLAM
--   + T_PHYSIKTYP
--   + T_HVT_TECHNIK
--
-- Besonderheiten:
--   + Pruefung, ob T_ENDSTELLE.ID=T_RANGIERUNG.ES_ID
--       ja:   ACTIVE_ON_ORDER=1
--       nein: ACTIVE_ON_ORDER=0
--   + Flags aufgenommen, ob es sich bei den Port-Daten
--     um EQ_IN oder EQ_OUT handelt
--


CREATE or REPLACE FORCE VIEW V_HURRICAN_PHYSIC
  AS SELECT
    r.RANGIER_ID as RANGIER_ID,
    e.ID as ACCESSPOINT_ID,
    r.EQ_IN_ID as EQ_IN_ID,
    r.EQ_OUT_ID as EQ_OUT_ID,
    r.HVT_ID_STANDORT as TECH_LOCATION_ID,
    r.GUELTIG_VON as RANGIER_VALID_FROM,
    r.GUELTIG_BIS as RANGIER_VALID_TO,
    r.PHYSIK_TYP as PHYSIC_TYPE_ID,
    pt.NAME as PHYSIK_TYPE,
    hwbgtyp.NAME as HW_PORT,
    eq.HW_EQN as HW_EQN,
    eq.HW_SCHNITTSTELLE as HW_INTERFACE,
    eq.RANG_VERTEILER as RANG_VERTEILER,
    eq.RANG_REIHE as RANG_REIHE,
    eq.RANG_BUCHT as RANG_BUCHT,
    eq.RANG_LEISTE1 as RANG_LEISTE1,
    eq.RANG_STIFT1 as RANG_STIFT1,
    eq.RANG_LEISTE2 as RANG_LEISTE2,
    eq.RANG_STIFT2 as RANG_STIFT2,
    eq.RANG_SS_TYPE as RANG_SS_TYPE,
    eq.UETV as UETV,
    eq.CARRIER as CARRIER,
    eq.V5_PORT AS V5_PORT,
    eq.KVZ_NUMMER AS KVZ_NUMMER,
    eq.KVZ_DA AS KVZ_DOPPELADER,
    eq.SCHICHT2_PROTOKOLL AS LAYER2_PROTOCOL,
    dlu.MG_NAME AS MEDIAGATEWAY,
    hwr.ID as DSLAM_ID,
    hwr.GERAETEBEZ || decode(hwr.MANAGEMENTBEZ, NULL, '', ' - '||hwr.MANAGEMENTBEZ) as DSLAM_NAME,
    hwd.ERX_STANDORT as ERX_LOCATION,
    hwd.IP_ADDRESS as DSLAM_IP,
    hwd.ANS_SLOT_PORT as DSLAM_SLOT_PORT,
    hwbgtyp.NAME as HARDWARE_BG_NAME,
    hwbgtyp.HW_SCHNITTSTELLE_NAME as HARDWARE_BG_SCHNITTSTELLE,
    ht.HERSTELLER as DSLAM_PRODUCER,
    sw.NAME as SWITCH,
    dpu.REVERSE_POWER AS PSE,
    CASE WHEN e.ID=r.ES_ID THEN 1 ELSE 0 END ACTIVE_ON_ORDER,
    CASE WHEN r.EQ_IN_ID=eq.EQ_ID THEN 1 ELSE 0 END IS_EQ_IN,
    CASE WHEN r.EQ_OUT_ID=eq.EQ_ID THEN 1 ELSE 0 END IS_EQ_OUT
  FROM
    T_ENDSTELLE e
    INNER JOIN T_RANGIERUNG r on (e.RANGIER_ID=r.RANGIER_ID or e.RANGIER_ID_ADDITIONAL=r.RANGIER_ID)
    LEFT JOIN T_PHYSIKTYP pt on r.PHYSIK_TYP=pt.ID
    LEFT JOIN T_EQUIPMENT eq on (r.EQ_OUT_ID=eq.EQ_ID or r.EQ_IN_ID=eq.EQ_ID)
    left join T_HW_SWITCH sw on (sw.ID = eq.SWITCH)
    LEFT JOIN T_HW_BAUGRUPPE hwbg on eq.HW_BAUGRUPPEN_ID=hwbg.ID
    LEFT JOIN T_HW_BAUGRUPPEN_TYP hwbgtyp on hwbg.HW_BG_TYP_ID=hwbgtyp.ID
    LEFT JOIN T_HW_RACK hwr ON (hwbg.RACK_ID = hwr.ID AND hwr.RACK_TYP IN ('DSLAM', 'DLU', 'MDU', 'ONT', 'DPO', 'DPU'))
    LEFT JOIN T_HW_RACK_DPU dpu on hwr.ID = dpu.RACK_ID
    LEFT JOIN T_HW_RACK_DSLAM hwd on hwr.ID=hwd.RACK_ID
    LEFT JOIN T_HW_RACK_DLU dlu on hwr.ID=dlu.RACK_ID
    LEFT JOIN T_HVT_STANDORT hs on eq.HVT_ID_STANDORT=hs.HVT_ID_STANDORT
    LEFT JOIN T_HVT_GRUPPE hg on hs.HVT_GRUPPE_ID=hg.HVT_GRUPPE_ID
    LEFT JOIN T_HVT_TECHNIK ht on hwr.HW_PRODUCER=ht.ID;


CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_PHYSIC FOR V_HURRICAN_PHYSIC;

GRANT SELECT ON V_HURRICAN_PHYSIC TO R_HURRICAN_BSI_VIEWS;