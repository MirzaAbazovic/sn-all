-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der Endgeraete, die auf
-- den technischen Auftraegen verbucht sind.
--
-- Verwendete Tabellen:
--   + T_EG
--   + T_EG_2_AUFTRAG
--   + T_EG_HERKUNFT
--   + T_MONTAGEART
--   + T_REFERENCE
--   + T_LIEFERSCHEIN
--
-- Besonderheiten:
--   + die Lieferschein-ID wird aus einem Prefix und der
--     eigentlichen ID zusammen gesetzt
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_DEVICE
  AS SELECT
    eg2a.ID as TECH_ORDER_DEVICE_ID,
    eg2a.AUFTRAG_ID as TECH_ORDER_ID,
    eg.ID as DEVICE_ID,
    eg.NAME as DEVICE_NAME,
    NULL as SERIAL_NUMBER,
    NULL as MAC_ADDRESS,
    ipAddr.ADDRESS as IP_ADDRESS,
    NULL as CUSTOMER_CARRAY_AWAY,
    NULL as EXPORT_DATE,
    NULL as TRACKING_NO,
    NULL as DEVICE_SOURCE_ID,
    NULL as DEVICE_SOURCE,
    m.ID as DEVICE_INSTALLATION_ID,
    m.NAME as DEVICE_INSTALLATION,
    NULL as SHIPMENT_TYPE_ID,
    NULL as SHIPMENT_TYPE,
    NULL as SHIPMENT_STATE_ID,
    NULL as SHIPMENT_STATE,
    NULL as SHIPMENT_ID,
    NULL as SHIPMENT_ADDRESS_ID,
    NULL as LIEFERSCHEINNUMMER,
    NULL as PAKET_NUMBER,
    NULL as SHIPMENT_DATE
  FROM
    T_EG_2_AUFTRAG eg2a
    INNER JOIN T_EG eg on eg2a.EG_ID=eg.ID
    LEFT JOIN T_MONTAGEART m on eg2a.MONTAGEART=m.ID
    LEFT JOIN T_IP_ADDRESS ipAddr ON (eg2a.IP_ADDRESS_ID = ipAddr.id
        AND ipAddr.GUELTIG_VON <= SYSDATE AND ipAddr.GUELTIG_BIS > SYSDATE);

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DEVICE FOR V_HURRICAN_DEVICE;

GRANT SELECT ON V_HURRICAN_DEVICE TO R_HURRICAN_BSI_VIEWS;

