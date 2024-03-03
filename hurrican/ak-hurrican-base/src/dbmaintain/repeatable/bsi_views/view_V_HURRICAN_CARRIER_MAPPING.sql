-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Carrier-Daten. Darunter
-- fallen der Carrier-Name sowie Kontaktinformationen des Carriers.
-- Ausserdem koennen noch Kunden-Informationen von M-net selbst
-- darin definiert/gemappt werden.
--
-- Verwendete Tabellen:
--   + T_CARRIER_MAPPING
--   + T_CARRIER_KENNUNG
--   + T_CARRIER_CONTACT
--   + T_CARRIER
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_CARRIER_MAPPING
  AS SELECT
    cm.ID AS CARRIER_MAPPING_ID,
    cm.CARRIER_ID as CARRIER_ID,
    cm.CARRIER_KENNUNG_ID as CARRIER_KENNUNG_ID,
    cm.CARRIER_CONTACT_ID as CARRIER_CONTACT_ID,
    c.TEXT
    || decode(cc.BRANCH_OFFICE, NULL, '', ' - '||cc.BRANCH_OFFICE)
    || decode(cc.RESSORT, NULL, '', ' '||cc.RESSORT)
    || decode(ck.BEZEICHNUNG, NULL, '', ' - '||ck.BEZEICHNUNG) as CARRIER_DESCRIPTION
  FROM
    T_CARRIER_MAPPING cm
    INNER JOIN T_CARRIER c on cm.CARRIER_ID=c.ID
    LEFT JOIN T_CARRIER_CONTACT cc on cm.CARRIER_CONTACT_ID=cc.ID
    LEFT JOIN T_CARRIER_KENNUNG ck on cm.CARRIER_KENNUNG_ID=ck.ID;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_CARRIER_MAPPING FOR V_HURRICAN_CARRIER_MAPPING;

GRANT SELECT ON V_HURRICAN_CARRIER_MAPPING TO R_HURRICAN_BSI_VIEWS;
