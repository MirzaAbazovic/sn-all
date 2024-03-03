-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2011 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung der IPs zu einem Endgeraet.
--
-- Verwendete Tabellen:
--   + T_EG_IP
--
-- Besonderheiten:
--

CREATE OR REPLACE FORCE VIEW V_HURRICAN_DEVICE_IP
(
   TECH_ORDER_DEVICE_ID,
   IP,
   SUBNET,
   ADDRESS_TYPE
)
AS
   SELECT eip.EG2A_ID AS TECH_ORDER_DEVICE_ID,
        ipAddr.ADDRESS AS IP,
        NULL AS SUBNET,
        eip.ADDRESS_TYPE AS ADDRESS_TYPE
   FROM T_EG_IP eip
     LEFT JOIN T_IP_ADDRESS ipAddr ON (eip.IP_ADDRESS_ID = ipAddr.id
        AND ipAddr.GUELTIG_VON <= SYSDATE AND ipAddr.GUELTIG_BIS > SYSDATE);


CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DEVICE_IP FOR V_HURRICAN_DEVICE_IP;

GRANT SELECT ON V_HURRICAN_DEVICE_IP TO R_HURRICAN_BSI_VIEWS;

