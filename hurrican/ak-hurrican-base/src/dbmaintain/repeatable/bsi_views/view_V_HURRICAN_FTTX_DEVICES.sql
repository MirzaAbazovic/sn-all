-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2014 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der ONT/DPO/MDU Daten.
--
-- Verwendete Tabellen:
--   + T_HW_RACK
--   + T_HW_RACK_MDU
--   + T_HW_RACK_ONT
--   + T_HW_BAUGRUPPE
--   + T_HVT_TECHNIK
--   + T_EQUIPMENT
--   + T_RANGIERUNG
--   + T_ENDSTELLE
--   + T_AUFTRAG_TECHNIK
--   + T_HW_RACK_OLT
--
-- Besonderheiten:
--

CREATE OR REPLACE FORCE VIEW V_HURRICAN_FTTX_DEVICES
AS
SELECT
    hwRack.GERAETEBEZ   AS NAME,
    hvtT.HERSTELLER     AS PRODUCER,
    (CASE hwRack.RACK_TYP
     WHEN 'MDU' THEN hwMdu.MDU_TYPE
     WHEN 'DPO' THEN hwDpo.DPO_TYPE
     WHEN 'ONT' THEN hwOnt.ONT_TYPE
     WHEN 'DPU' THEN hwDpu.DPU_TYPE
     END)               AS TYPE,
    eq.HW_EQN           AS PORT,
    eq.HW_SCHNITTSTELLE AS PORT_TYPE,
    (CASE hwRack.RACK_TYP
     WHEN 'MDU' THEN hwMdu.SERIAL_NO
     WHEN 'DPO' THEN hwDpo.SERIAL_NO
     WHEN 'ONT' THEN hwOnt.SERIAL_NO
     WHEN 'DPU' THEN hwDpu.DPU_TYPE
     END)               AS SERIAL_NO,
    (CASE hwRack.RACK_TYP
     WHEN 'MDU' THEN
       CASE WHEN hwMdu.OLT_SUBRACK IS NULL THEN
         hwMdu.OLT_FRAME || '-' || hwMdu.OLT_SHELF || '-' || hwMdu.OLT_GPON_PORT || '-' || hwMdu.OLT_GPON_ID
       ELSE
         hwMdu.OLT_FRAME || '-' || hwMdu.OLT_SUBRACK || '-' || hwMdu.OLT_SHELF || '-' || hwMdu.OLT_GPON_PORT || '-' ||
         hwMdu.OLT_GPON_ID
       END
     WHEN 'DPO' THEN
       CASE WHEN hwDpo.OLT_SUBRACK IS NULL THEN
         hwDpo.OLT_FRAME || '-' || hwDpo.OLT_SHELF || '-' || hwDpo.OLT_GPON_PORT || '-' || hwDpo.OLT_GPON_ID
       ELSE
         hwDpo.OLT_FRAME || '-' || hwDpo.OLT_SUBRACK || '-' || hwDpo.OLT_SHELF || '-' || hwDpo.OLT_GPON_PORT || '-' ||
         hwDpo.OLT_GPON_ID
       END
     WHEN 'DPU' THEN
       CASE WHEN hwDpu.OLT_SUBRACK IS NULL THEN
         hwDpu.OLT_FRAME || '-' || hwDpu.OLT_SHELF || '-' || hwDpu.OLT_GPON_PORT || '-' || hwDpu.OLT_GPON_ID
       ELSE
         hwDpu.OLT_FRAME || '-' || hwDpu.OLT_SUBRACK || '-' || hwDpu.OLT_SHELF || '-' || hwDpu.OLT_GPON_PORT || '-' ||
         hwDpu.OLT_GPON_ID
       END
     WHEN 'ONT' THEN
       CASE WHEN hwOnt.OLT_SUBRACK IS NULL THEN
         hwOnt.OLT_FRAME || '-' || hwOnt.OLT_SHELF || '-' || hwOnt.OLT_GPON_PORT || '-' || hwOnt.OLT_GPON_ID
       ELSE
         hwOnt.OLT_FRAME || '-' || hwOnt.OLT_SUBRACK || '-' || hwOnt.OLT_SHELF || '-' || hwOnt.OLT_GPON_PORT || '-' ||
         hwOnt.OLT_GPON_ID
       END
     END)               AS OLT_PORT,
    (CASE WHEN hwRackOlt.GERAETEBEZ IS NOT NULL THEN
      hwRackOlt.GERAETEBEZ
     ELSE
       hwRackDslam.GERAETEBEZ
     END)               AS OLT_NAME,
    (CASE WHEN hvtTOlt.HERSTELLER IS NOT NULL THEN
      hvtTOlt.HERSTELLER
     ELSE
       hvtTDslam.HERSTELLER
     END)               AS OLT_PRODUCER,
    at.AUFTRAG_ID       AS TECH_ORDER_ID
  FROM T_HW_RACK hwRack
    LEFT JOIN T_HW_RACK_MDU hwMdu ON hwMdu.rack_id = hwRack.id
    LEFT JOIN T_HW_RACK_ONT hwOnt ON hwOnt.rack_id = hwRack.id
    LEFT JOIN T_HW_RACK_DPO hwDpo ON hwDpo.rack_id = hwRack.id
    LEFT JOIN T_HW_RACK_DPU hwDpu ON hwDpu.RACK_ID = hwRack.ID
    LEFT JOIN T_HW_BAUGRUPPE hwBg ON hwBg.rack_id = hwRack.id
    LEFT JOIN T_HVT_TECHNIK hvtT ON hwRack.hw_producer = hvtT.id
    LEFT JOIN T_EQUIPMENT eq ON eq.hw_baugruppen_id = hwBg.id
    LEFT JOIN T_RANGIERUNG rang ON rang.eq_in_id = eq.eq_id
    LEFT JOIN T_ENDSTELLE es ON es.rangier_id = rang.rangier_id
    LEFT JOIN T_AUFTRAG_TECHNIK at ON at.AT_2_ES_ID = es.es_gruppe
    LEFT JOIN T_HW_RACK_OLT hwOlt ON (hwMdu.olt_rack_id IS NOT NULL AND hwMdu.olt_rack_id = hwOlt.rack_id)
                                     OR (hwOnt.olt_rack_id IS NOT NULL AND hwOnt.olt_rack_id = hwOlt.rack_id)
                                     OR (hwDpo.olt_rack_id IS NOT NULL AND hwDpo.olt_rack_id = hwOlt.rack_id)
                                     OR (hwDpu.OLT_RACK_ID IS NOT NULL AND hwDpu.OLT_RACK_ID = hwOlt.RACK_ID)
    LEFT JOIN T_HW_RACK hwRackOlt ON hwOlt.rack_id = hwRackOlt.id
    LEFT JOIN T_HW_RACK hwRackDslam ON (hwMdu.olt_rack_id IS NOT NULL AND hwMdu.olt_rack_id = hwRackDslam.id)
                                       OR (hwOnt.olt_rack_id IS NOT NULL AND hwOnt.olt_rack_id = hwRackDslam.id)
                                       OR (hwDpo.olt_rack_id IS NOT NULL AND hwDpo.olt_rack_id = hwRackDslam.id)
                                       OR (hwDpu.OLT_RACK_ID IS NOT NULL AND hwDpu.OLT_RACK_ID = hwRackDslam.ID)
    LEFT JOIN T_HVT_TECHNIK hvtTOlt ON hwRackOlt.hw_producer = hvtTOlt.id
    LEFT JOIN T_HVT_TECHNIK hvtTDslam ON hwRackDslam.hw_producer = hvtTDslam.id
  WHERE (rang.gueltig_von IS NULL OR rang.gueltig_von <= SYSDATE)
        AND (rang.gueltig_bis IS NULL OR rang.gueltig_bis > SYSDATE)
        AND at.gueltig_von <= SYSDATE AND at.gueltig_bis > SYSDATE
        AND hwRack.RACK_TYP IN ('MDU', 'ONT', 'DPO', 'DPU');

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_FTTX_DEVICES FOR V_HURRICAN_FTTX_DEVICES;

GRANT SELECT ON V_HURRICAN_FTTX_DEVICES TO R_HURRICAN_BSI_VIEWS;
