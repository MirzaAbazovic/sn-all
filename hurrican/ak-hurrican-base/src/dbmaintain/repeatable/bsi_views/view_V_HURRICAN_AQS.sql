-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Aderquerschnitten (AQS) bzw.
-- Leitungslaengen von (bei DTAG) durchgefuehrten TAL-Bestellungen.
--
-- Verwendete Tabellen:
--  + T_ENDSTELLE
--  + T_CARRIERBESTELLUNG
--  + T_GEO_ID_CACHE
--  + T_HVT_STANDORT
--  + T_HVT_GRUPPE
--  + T_REFERENCE
--
-- Besonderheiten:
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_AQS
  AS SELECT
    g.ID as GEO_ID,
    hg.ONKZ as ONKZ,
    hs.ASB as ASB,
    e.ID as ACCESSPOINT_ID,
    e.ENDSTELLE as ACCESSPOINT,
    cb.AQS as AQS,
	cb.LL as LENGTH,
	ref.STR_VALUE as TECH_LOCATION_TYPE
  FROM
    T_ENDSTELLE e
    INNER JOIN T_CARRIERBESTELLUNG cb on e.CB_2_ES_ID=cb.CB_2_ES_ID
    INNER JOIN T_GEO_ID_CACHE g on e.GEO_ID=g.ID
    INNER JOIN T_HVT_STANDORT hs on e.HVT_ID_STANDORT=hs.HVT_ID_STANDORT
    INNER JOIN T_HVT_GRUPPE hg on hs.HVT_GRUPPE_ID=hg.HVT_GRUPPE_ID
    INNER JOIN T_REFERENCE ref on hs.STANDORT_TYP_REF_ID=ref.ID
  WHERE
    e.GEO_ID is not null
    and (cb.AQS is not null OR cb.LL is not null)
    and hs.GUELTIG_BIS > SYSDATE;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_AQS FOR V_HURRICAN_AQS;

GRANT SELECT ON V_HURRICAN_AQS TO R_HURRICAN_BSI_VIEWS;
