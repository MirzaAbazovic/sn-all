
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

GRANT SELECT ON V_HURRICAN_SERVICE_PROVIDER TO R_HURRICAN_BSI_VIEWS;