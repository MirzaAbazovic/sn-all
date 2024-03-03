-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der zu einer Endgeraetekonfiguration
-- erfassten Port-Forwardings.
--
-- Verwendete Tabellen:
--   + T_PORT_FORWARD
--
-- Besonderheiten:
--

CREATE OR REPLACE FORCE VIEW V_HURRICAN_DEVICE_CFG_PORT
(
   DEVICE_CONFIG_ID,
   PROTOCOL_ID,
   TRANSPORT_PROTOCOL,
   IP_ADDRESS,
   IS_ACTIVE,
   DESCRIPTION,
   SOURCE_IP_ADDRESS,
   DEST_IP_ADDRESS,
   SOURCE_PORT,
   DEST_PORT,
   OPERATOR
)
AS
   SELECT pf.EG_CONFIG_ID AS DEVICE_CONFIG_ID,
          NULL AS PROTOCOL_ID,
          pf.TRANSPORT_PROTOCOL AS TRANSPORT_PROTOCOL,
          destIp.ADDRESS AS DEST_IP_ADDRESS,
          pf.ACTIVE AS IS_ACTIVE,
          pf.BEMERKUNG AS DESCRIPTION,
          sourceIp.ADDRESS AS SOURCE_IP_ADDRESS,
          destIp.ADDRESS AS DEST_IP_ADDRESS,
          pf.SOURCE_PORT AS SOURCE_PORT,
          pf.DEST_PORT AS DEST_PORT,
          pf.BEARBEITER AS OPERATOR
   FROM   T_PORT_FORWARD pf
     LEFT JOIN T_IP_ADDRESS destIp ON (pf.DEST_IP_ADDRESS_ID = destIp.id
        AND destIp.GUELTIG_VON <= SYSDATE AND destIp.GUELTIG_BIS > SYSDATE)
     LEFT JOIN T_IP_ADDRESS sourceIp ON (pf.SOURCE_IP_ADDRESS_ID = sourceIp.id
        AND sourceIp.GUELTIG_VON <= SYSDATE AND sourceIp.GUELTIG_BIS > SYSDATE);

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DEVICE_CFG_PORT FOR V_HURRICAN_DEVICE_CFG_PORT;

GRANT SELECT ON V_HURRICAN_DEVICE_CFG_PORT TO R_HURRICAN_BSI_VIEWS;
