-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der IP-Adressen, die einem
-- technischen Auftrag zugeordnet sind.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_DATEN
--   + T_IP_ADDRESS

CREATE OR REPLACE FORCE VIEW V_HURRICAN_IP
(
   TECH_ORDER_ID,
   IP_ADDRESS,
   IP_ADDRESS_BIN,
   PRODAK_ORDER__NO
)
AS
  SELECT ad.AUFTRAG_ID AS TECH_ORDER_ID, ipAddr.ADDRESS AS IP_ADDRESS,
    ipAddr.BINARY_REPRESENTATION as IP_ADDRESS_BIN, ad.PRODAK_ORDER__NO
  FROM T_AUFTRAG_DATEN ad
    INNER JOIN T_IP_ADDRESS ipAddr ON (ad.PRODAK_ORDER__NO = ipAddr.BILLING_ORDER_NO
      AND ipAddr.GUELTIG_VON <= SYSDATE AND ipAddr.GUELTIG_BIS > SYSDATE)
  WHERE ad.GUELTIG_BIS = TO_DATE('01.01.2200', 'dd.mm.yyyy');

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_IP FOR V_HURRICAN_IP;

GRANT SELECT ON V_HURRICAN_IP TO R_HURRICAN_BSI_VIEWS;
