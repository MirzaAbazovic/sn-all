-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der fuer einen Auftrag
-- gueltigen Accounts.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_DATEN
--   + T_AUFTRAG_TECHNIK
--   + T_INT_ACCOUNT
--   + T_ACC_ART
--
-- Besonderheiten:
--   + Auftragsstatus "10000" (Konsolidiert) ausgeschlossen
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_ACCOUNTS
  AS SELECT
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    acc.ID as ACCOUNT_ID,
    acc.ACCOUNT as ACCOUNT,
    acc.PASSWORT as ACCOUNT_PASSWORD,
    acc.GESPERRT as IS_LOCKED,
    acc.RUFNUMMER as DIAL_NUMBER,
    acca.LI_NR as ACCOUNT_TYPE_ID,
    acca.TEXT as ACCOUNT_TYPE
  FROM
    T_AUFTRAG_DATEN ad
    INNER JOIN T_AUFTRAG_TECHNIK atech on ad.AUFTRAG_ID=atech.AUFTRAG_ID
    INNER JOIN T_INT_ACCOUNT acc on atech.INT_ACCOUNT_ID=acc.ID
    INNER JOIN T_ACC_ART acca on acc.LI_NR=acca.LI_NR
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    and ad.STATUS_ID < 10000
    and atech.GUELTIG_BIS > SYSDATE
    and acc.GUELTIG_BIS > SYSDATE;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_ACCOUNTS FOR V_HURRICAN_ACCOUNTS;

GRANT SELECT ON V_HURRICAN_ACCOUNTS TO R_HURRICAN_BSI_VIEWS;
