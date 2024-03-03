-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2011 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Zusammenfassung verschiedenster
-- Suchfelder in BSI (RFC-813).
--
-- Verwendete Tabellen:
-- T_AUFTRAG
-- T_AUFTRAG_DATEN
-- T_AUFTRAG_TECHNIK
-- T_TDN
-- T_ENDSTELLE
-- T_CARRIERBESTELLUNG
-- T_LEITUNGSNUMMER
--
-- Besonderheiten:
--   + Auftragsstatus "10000" (Konsolidiert) ausgeschlossen
--
-- Hinweise:


CREATE or REPLACE FORCE VIEW V_HURRICAN_COMBINED_SEARCH
  AS SELECT
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    tdn.TDN as VALUE,
    'VBZ' as VALUE_TYPE
  FROM
    T_AUFTRAG_DATEN ad
    INNER JOIN T_AUFTRAG_TECHNIK atech on ad.AUFTRAG_ID=atech.AUFTRAG_ID
    INNER JOIN T_TDN tdn on atech.TDN_ID=tdn.ID
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and atech.GUELTIG_BIS > SYSDATE
    and tdn.TDN is NOT NULL
  UNION
  SELECT
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    cb.LBZ as VALUE,
    'LBZ' as VALUE_TYPE
  FROM
    T_AUFTRAG_DATEN ad
    INNER JOIN T_AUFTRAG_TECHNIK atech on ad.AUFTRAG_ID=atech.AUFTRAG_ID
    INNER JOIN T_ENDSTELLE es on atech.AT_2_ES_ID=es.ES_GRUPPE
    INNER JOIN T_CARRIERBESTELLUNG cb on cb.CB_2_ES_ID=es.CB_2_ES_ID
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and atech.GUELTIG_BIS > SYSDATE
    and cb.LBZ is NOT NULL
  UNION
  SELECT
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    cb.VTRNR as VALUE,
    'VERTRAGSNUMMER' as VALUE_TYPE
  FROM
    T_AUFTRAG_DATEN ad
    INNER JOIN T_AUFTRAG_TECHNIK atech on ad.AUFTRAG_ID=atech.AUFTRAG_ID
    INNER JOIN T_ENDSTELLE es on atech.AT_2_ES_ID=es.ES_GRUPPE
    INNER JOIN T_CARRIERBESTELLUNG cb on cb.CB_2_ES_ID=es.CB_2_ES_ID
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and atech.GUELTIG_BIS > SYSDATE
    and cb.VTRNR is NOT NULL
  UNION
  SELECT
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    ad.LBZ_KUNDE as VALUE,
    'LBZ-KUNDE' as VALUE_TYPE
  FROM
    T_AUFTRAG_DATEN ad
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and LBZ_KUNDE is NOT NULL
  UNION
  SELECT
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    ad.BESTELLNR as VALUE,
    'BESTELL-NR' as VALUE_TYPE
  FROM
    T_AUFTRAG_DATEN ad
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and ad.BESTELLNR is NOT NULL
  UNION
  SELECT
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    ln.LEITUNGSNUMMER as VALUE,
    'LBZ-CARRIER' as VALUE_TYPE
  FROM
    T_AUFTRAG a
    INNER JOIN T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID
    INNER JOIN T_AUFTRAG_TECHNIK atech on a.ID=atech.AUFTRAG_ID
    INNER JOIN T_LEITUNGSNUMMER ln on a.ID=ln.AUFTRAG_ID
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and atech.GUELTIG_BIS > SYSDATE
    and ln.LEITUNGSNUMMER is NOT NULL;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_COMBINED_SEARCH FOR V_HURRICAN_COMBINED_SEARCH;

GRANT SELECT ON V_HURRICAN_COMBINED_SEARCH TO R_HURRICAN_BSI_VIEWS;
