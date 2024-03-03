-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2011 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von GeoID zu ONKZ / ASB
--
-- Verwendete Tabellen:
--   + T_GEO_ID_CACHE
--   + T_GEO_ID_2_TECH_LOCATION
--   + T_HVT_STANDORT
--   + T_HVT_GRUPPE
--   + T_GEO_ID_STREET_SECTION
--   + T_GEO_ID_ZIPCODE
--
-- Besonderheiten:
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_GEO_2_ASB
  AS SELECT
    geo.ID as GEO_ID,
    HG.ONKZ as ONKZ,
    HST.ASB as ASB,
    HST.CLUSTER_ID as CLUSTER_ID,
    NL.AREA_NO as AREA_NO,
    zipcode.ZIP_CODE as ZIPCODE
  from T_GEO_ID_CACHE geo
    inner join T_GEO_ID_2_TECH_LOCATION geotl on geo.id=GEOTL.GEO_ID
    inner join T_HVT_STANDORT hst on GEOTL.HVT_ID_STANDORT=HST.HVT_ID_STANDORT
    inner join T_HVT_GRUPPE hg on HST.HVT_GRUPPE_ID=HG.HVT_GRUPPE_ID
    inner join T_NIEDERLASSUNG nl on HG.NIEDERLASSUNG_ID=NL.ID
    INNER JOIN T_GEO_ID_STREET_SECTION street ON geo.STREETSECTION_ID=street.ID
    INNER JOIN T_GEO_ID_ZIPCODE zipcode ON street.ZIP_CODE_ID=zipcode.ID;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_GEO_2_ASB FOR V_HURRICAN_GEO_2_ASB;

GRANT SELECT ON V_HURRICAN_GEO_2_ASB TO R_HURRICAN_BSI_VIEWS;
