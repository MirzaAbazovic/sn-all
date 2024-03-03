-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung der QoS-Daten zu einem techn. Auftrag.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_QOS
--   + T_REFERENCE
--
-- Besonderheiten:
--   + Es werden alle QoS-Daten zu einem Auftrag mit entsprechendem
--     Gueltigkeitsdatum angezeigt
--   + Sortierung erfolgt ueber das Gueltigkeitsdatum (aufsteigend)
--


CREATE or REPLACE FORCE VIEW V_HURRICAN_QOS
  AS SELECT
    qos.AUFTRAG_ID as TECH_ORDER_ID,
    qos.QOS_CLASS_REF_ID as QOS_CLASS_ID,
    r.STR_VALUE as QOS_CLASS,
    qos.PERCENTAGE as PERCENTAGE,
    qos.GUELTIG_VON as VALID_FROM,
    qos.GUELTIG_BIS as VALID_TO
  FROM
    T_AUFTRAG_QOS qos
    INNER JOIN T_REFERENCE r on qos.QOS_CLASS_REF_ID=r.ID
  ORDER BY
    qos.GUELTIG_VON ASC,
    qos.GUELTIG_BIS ASC;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_QOS FOR V_HURRICAN_QOS;

GRANT SELECT ON V_HURRICAN_QOS TO R_HURRICAN_BSI_VIEWS;
