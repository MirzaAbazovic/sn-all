-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2010 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung von Kontaktdaten von externen Dienstleistern.
-- Diese Kontaktdaten koennen z.B. fuer Stoerungsmeldungen verwendet werden.
--
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_SERVICE_PROVIDER
  AS SELECT
    ID as SERVICE_PROVIDER_ID,
    NAME,
    FIRSTNAME,
    STREET,
    HOUSE_NUM,
    POSTAL_CODE,
    CITY,
    PHONE,
    MAIL,
    FAX
  FROM
    T_EXT_SERVICE_PROVIDER;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_SERVICE_PROVIDER FOR V_HURRICAN_SERVICE_PROVIDER;

GRANT SELECT ON V_HURRICAN_SERVICE_PROVIDER TO R_HURRICAN_BSI_VIEWS;
