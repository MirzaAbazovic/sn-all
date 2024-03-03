-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Hurrican-Adressen.
--
-- Verwendete Tabellen:
--  + T_ADDRESS
--
-- Besonderheiten:
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_ADDRESS
  AS SELECT
    adr.ID,
    adr.KUNDE__NO,
    adr.NAME,
    adr.VORNAME,
    adr.NAME2,
    adr.VORNAME2,
    adr.STRASSE,
    adr.STRASSE_ADD,
    adr.HAUSNUMMER,
    adr.HAUSNUMMER_ZUSATZ,
    adr.PLZ,
    adr.ORT,
    adr.ORT_ZUSATZ,
    adr.POSTFACH,
    adr.LAND_ID,
    adr.TELEFON as PHONE,
    adr.HANDY as MOBILE,
    adr.FAX as FAX,
    adr.EMAIL as EMAIL,
    adr.TITEL as TITLE,
    adr.TITEL2 as TITLE2,
    adr.BEMERKUNG as DESCRIPTION,
    adr.PRIO_BRIEF,
	adr.PRIO_EMAIL,
	adr.PRIO_FAX,
	adr.PRIO_SMS,
	adr.PRIO_TEL
FROM
    T_ADDRESS adr;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_ADDRESS FOR V_HURRICAN_ADDRESS;

GRANT SELECT ON V_HURRICAN_ADDRESS TO R_HURRICAN_BSI_VIEWS;
