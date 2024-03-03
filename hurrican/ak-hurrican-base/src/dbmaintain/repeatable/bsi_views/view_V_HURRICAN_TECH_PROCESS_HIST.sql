-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der Verlaufs-Historie zu
-- den technischen Auftraegen.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_DATEN
--   + T_VERLAUF
--   + T_VERLAUF_STATUS
--   + T_BA_VERL_ANLASS
--
-- Besonderheiten:
--   + Auftragsstatus "10000" (Konsolidiert) ausgeschlossen
--


CREATE or REPLACE FORCE VIEW V_HURRICAN_TECH_PROCESS_HIST
  AS SELECT
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    v.ID as PROCESS_ID,
    v.ANLASS as PROCESS_TYPE_ID,
    bva.NAME as PROCESS_TYPE,
    v.VERLAUF_STATUS_ID as PROCESS_STATE_ID,
    vs.VERLAUF_STATUS as PROCESS_STATE,
    v.PROJEKTIERUNG as IS_PROJECT,
    v.AKT as IS_AKT,
    v.REALISIERUNGSTERMIN as PLANNED_REAL_DATE
  FROM
    T_AUFTRAG_DATEN ad
    INNER JOIN T_VERLAUF v on ad.AUFTRAG_ID=v.AUFTRAG_ID
    INNER JOIN T_BA_VERL_ANLASS bva on v.ANLASS=bva.ID
    INNER JOIN T_VERLAUF_STATUS vs on v.VERLAUF_STATUS_ID=vs.ID
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_TECH_PROCESS_HIST FOR V_HURRICAN_TECH_PROCESS_HIST;

GRANT SELECT ON V_HURRICAN_TECH_PROCESS_HIST TO R_HURRICAN_BSI_VIEWS;
