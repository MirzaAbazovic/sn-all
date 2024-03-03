-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Carrier-Kennungen. Hier
-- handelt es sich um Daten der M-net, wie sie bei den
-- Carriern (speziell DTAG) hinterlegt sind.
-- Hintergrund: jede M-net Niederlassung hat bei DTAG aktuell
-- noch unterschiedliche Kundennummern.
--
-- Verwendete Tabellen:
--   + T_CARRIER_KENNUNG
--   + T_CARRIER
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_CARRIER_KENNUNG
  AS SELECT
    ck.ID as CARRIER_KENNUNG_ID,
    ck.CARRIER_ID as CARRIER_ID,
    c.TEXT as CARRIER,
    ck.BEZEICHNUNG as SHORT_NAME,
    ck.KUNDE_NR as CUSTOMER_NO_4_CARRIER,
    ck.NAME as CUSTOMER_NAME_4_CARRIER,
    ck.STRASSE as CUSTOMER_STREET_4_CARRIER,
    ck.PLZ as CUSTOMER_ZIPCODE_4_CARRIER,
    ck.ORT as CUSTOMER_CITY_4_CARRIER
  FROM
    T_CARRIER_KENNUNG ck
    INNER JOIN T_CARRIER c on ck.CARRIER_ID=c.ID;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_CARRIER_KENNUNG FOR V_HURRICAN_CARRIER_KENNUNG;

GRANT SELECT ON V_HURRICAN_CARRIER_KENNUNG TO R_HURRICAN_BSI_VIEWS;
