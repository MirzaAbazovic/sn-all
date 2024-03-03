-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2009 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Hurrican-Ansprechpartnern.
--
-- Verwendete Tabellen:
--  + T_ANSPRECHPARTNER
--  + T_AUFTRAG_DATEN
--  + T_REFERENCE
--
-- Besonderheiten:
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_CONTACT_PERSON
  AS SELECT
    ad.AUFTRAG_ID as TECH_ORDER_ID,
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ap.ID as CONTACT_ID,
    ap.TYPE_REF_ID as CONTACT_TYPE,
    ref.STR_VALUE as CONTACT_TYPE_NAME,
    ap.PREFERRED as PREFERRED_CONTACT,
    ap.TEXT as CONTACT_INFO,
    ap.ADDRESS_ID as CONTACT_ADDRESS,
    ap.PRIO
  FROM
    T_ANSPRECHPARTNER ap
    inner join T_AUFTRAG_DATEN ad on ad.AUFTRAG_ID=ap.AUFTRAG_ID
    inner join T_REFERENCE ref on ap.TYPE_REF_ID=ref.ID
  where ad.GUELTIG_BIS>SYSDATE;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_CONTACT_PERSON FOR V_HURRICAN_CONTACT_PERSON;

GRANT SELECT ON V_HURRICAN_CONTACT_PERSON TO R_HURRICAN_BSI_VIEWS;
