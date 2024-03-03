-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung von Rufnummernleistungen.
--
-- Verwendete Tabellen:
--   + T_LEISTUNG_DN
--   + T_LEISTUNG_4_DN
--   + T_LEISTUNG_PARAMETER
--
-- Besonderheiten:
--   + die Verbindung zur Rufnummer muss ueber TAIFUN.DN.DN_NO
--     auf V_HURRICAN_DN_SERVICES.DN_NO hergestellt werden
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_DN_SERVICES
  AS SELECT
    ldn.DN_NO as DN_NO,
    l.ID as DN_SERVICE_ID,
    l.LEISTUNG as DN_SERVICE,
    lp.ID as DN_SERVICE_PARAM_ID,
    lp.BESCHREIBUNG as DN_SERVICE_PARAM,
    ldn.LEISTUNG_PARAMETER as DN_SERVICE_PARAM_VALUE,
    ldn.EWSD_REALISIERUNG as REAL_DATE,
    ldn.EWSD_KUENDIGUNG as CANCEL_DATE
  FROM
    T_LEISTUNG_DN ldn
    INNER JOIN T_LEISTUNG_4_DN l on ldn.LEISTUNG4DN_ID=l.ID
    LEFT JOIN T_LEISTUNG_PARAMETER lp on ldn.PARAMETER_ID=lp.ID;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DN_SERVICES FOR V_HURRICAN_DN_SERVICES;

GRANT SELECT ON V_HURRICAN_DN_SERVICES TO R_HURRICAN_BSI_VIEWS;
