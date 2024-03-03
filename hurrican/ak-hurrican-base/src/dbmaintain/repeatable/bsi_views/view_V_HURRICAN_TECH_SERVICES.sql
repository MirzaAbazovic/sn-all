-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung von technischen Leistungen
-- zu den Auftraegen.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_DATEN
--   + T_TECH_LEISTUNG
--   + T_AUFTRAG_2_TECH_LS
--
-- Besonderheiten:
--   + Auftragsstatus "10000" (Konsolidiert) ausgeschlossen
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_TECH_SERVICES
  AS SELECT
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    tl.NAME as TECH_SERVICE_NAME,
    tl.ID as TECH_SERVICE_ID,
    tl.TYP as TECH_SERVICE_TYP,
    a2tl.QUANTITY as TECH_SERVICE_QUANTITY,
    a2tl.AKTIV_VON as VALID_FROM,
    a2tl.AKTIV_BIS as VALID_TO,
    a2tl.VERLAUF_ID_REAL as REAL_PROCESS_ID,
    a2tl.VERLAUF_ID_KUEND as CANCEL_PROCESS_ID
  FROM
    T_AUFTRAG_DATEN ad
    INNER JOIN T_AUFTRAG_2_TECH_LS a2tl on ad.AUFTRAG_ID=a2tl.AUFTRAG_ID
    INNER JOIN T_TECH_LEISTUNG tl on a2tl.TECH_LS_ID=tl.ID
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_TECH_SERVICES FOR V_HURRICAN_TECH_SERVICES;

GRANT SELECT ON V_HURRICAN_TECH_SERVICES TO R_HURRICAN_BSI_VIEWS;
