-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Kontaktdaten unterschiedlicher
-- Carrier.
-- Diese Kontaktdaten koennen z.B. fuer Stoerungsmeldungen verwendet werden.
--
-- Verwendete Tabellen:
--   + T_CARRIER_CONTACT
--   + T_CARRIER
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_CARRIER_CONTACT
  AS SELECT
    co.ID as CARRIER_CONTACT_ID,
    co.CARRIER_ID as CARRIER_ID,
    c.COMPANY_NAME as CARRIER_COMPANY_NAME,
    c.TEXT || ' - ' || co.BRANCH_OFFICE as CONTACT_NAME,
    co.BRANCH_OFFICE as CARRIER_OFFICE,
    co.RESSORT as CARRIER_RESSORT,
    co.CONTACT_NAME as CARRIER_TEAM_NAME,
    co.FAULT_CLEARING_PHONE as CARRIER_FAULT_CLEARING_PHONE,
    co.FAULT_CLEARING_FAX as CARRIER_FAULT_CLEARING_FAX,
    co.FAULT_CLEARING_EMAIL as CARRIER_FAULT_CLEARING_EMAIL
  FROM
    T_CARRIER_CONTACT co
    INNER JOIN T_CARRIER c on co.CARRIER_ID=c.ID;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_CARRIER_CONTACT FOR V_HURRICAN_CARRIER_CONTACT;

GRANT SELECT ON V_HURRICAN_CARRIER_CONTACT TO R_HURRICAN_BSI_VIEWS;
