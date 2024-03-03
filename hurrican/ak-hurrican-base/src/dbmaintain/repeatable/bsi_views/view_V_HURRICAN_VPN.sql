-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2011 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung der VPN Daten zu einem Auftrag.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_DATEN
--   + T_AUFTRAG_TECHNIK
--   + T_VPN
--
-- Besonderheiten:
--

CREATE OR REPLACE FORCE VIEW V_HURRICAN_VPN
(
   TECH_ORDER_ID,
   VPN_NAME
)
AS
   SELECT atech.AUFTRAG_ID AS TECH_ORDER_ID,
        vpn.VPN_NAME AS VPN_NAME
  FROM
    T_AUFTRAG_DATEN ad
    INNER JOIN T_AUFTRAG_TECHNIK atech on ad.AUFTRAG_ID=atech.AUFTRAG_ID
    INNER JOIN T_VPN vpn on atech.VPN_ID=vpn.VPN_ID
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and atech.GUELTIG_BIS > SYSDATE;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_VPN FOR V_HURRICAN_VPN;

GRANT SELECT ON V_HURRICAN_VPN TO R_HURRICAN_BSI_VIEWS;
