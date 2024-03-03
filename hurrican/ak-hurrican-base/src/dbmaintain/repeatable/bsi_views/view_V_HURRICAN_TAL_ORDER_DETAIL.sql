-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Details von elektronisch
-- ausgefuehrten TAL-Bestellungen.
--
-- Verwendete Tabellen:
--   + T_CB_VORGANG
--   + T_CARRIER
--   + T_REFERENCE
--
-- Besonderheiten:
--
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_TAL_ORDER_DETAIL
  AS SELECT
    cbv.CB_ID as TAL_ORDER_ID,
    cbv.AUFTRAG_ID as TECH_ORDER_ID,
    cbv.CARRIER_ID as CARRIER_ID,
    c.TEXT as CARRIER,
    cbv.TYP as TAL_ORDER_TYPE_ID,
    reft.STR_VALUE as TAL_ORDER_TYPE,
    cbv.STATUS as TAL_ORDER_STATE_ID,
    refs.STR_VALUE as TAL_ORDER_STATE,
    cbv.BEZEICHNUNG_MNET as MNET_REFERENCE_KEY,
    cbv.VORGABE_MNET as WISH_DATE,
    cbv.MONTAGEHINWEIS as MNET_COMMENT,
    cbv.CARRIER_REF_NR as CARRIER_REFERENCE_KEY,
    cbv.RET_OK as RETURN_OK,
    cbv.RET_LBZ as RETURN_LBZ,
    cbv.RET_VTRNR as RETURN_VTRNR,
    cbv.RET_AQS as RETURN_AQS,
    cbv.RET_LL as RETURN_LL,
    cbv.RET_KUNDE_VOR_ORT as RETURN_CUSTOMER_AT_LOCATION,
    cbv.RET_REAL_DATE as RETURN_REAL_DATE,
    cbv.RET_BEMERKUNG as RETURN_COMMENT,
    cbv.SUBMITTED_AT as ORDER_SUBMIT_DATE,
    cbv.ANSWERED_AT as ORDER_ANSWER_DATE,
    cbv.BEARBEITER as MNET_USER,
    cbv.CARRIER_BEARBEITER as CARRIER_USER,
    cbv.STATUS_BEMERKUNG as WORKING_COMMENT
  FROM
    T_CB_VORGANG cbv
    INNER JOIN T_CARRIER c on cbv.CARRIER_ID=c.ID
    INNER JOIN T_REFERENCE reft on cbv.TYP=reft.ID
    INNER JOIN T_REFERENCE refs on cbv.STATUS=refs.ID;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_TAL_ORDER_DETAIL FOR V_HURRICAN_TAL_ORDER_DETAIL;

GRANT SELECT ON V_HURRICAN_TAL_ORDER_DETAIL TO R_HURRICAN_BSI_VIEWS;
