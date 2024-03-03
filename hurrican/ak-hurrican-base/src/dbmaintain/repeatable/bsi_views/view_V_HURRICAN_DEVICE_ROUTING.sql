-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2011 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung der Statischen Routen zu einem Endgeraet.
--
-- Verwendete Tabellen:
--   + T_EG_ROUTING
--
-- Besonderheiten:
--

CREATE OR REPLACE FORCE VIEW V_HURRICAN_DEVICE_ROUTING
(
   TECH_ORDER_DEVICE_ID,
   DEST_ADDRESS,
   SUBNET,
   NEXT_HOP,
   DESCRIPTION
)
AS
   SELECT er.EG2A_ID AS TECH_ORDER_DEVICE_ID,
        ipAddr.ADDRESS AS DEST_ADDRESS,
        NULL AS SUBNET,
        er.NEXT_HOP AS NEXT_HOP,
        er.BEMERKUNG AS DESCRIPTION
   FROM T_EG_ROUTING er
     LEFT JOIN T_IP_ADDRESS ipAddr ON (er.DESTINATION_ADRESS_ID = ipAddr.id
        AND ipAddr.GUELTIG_VON <= SYSDATE AND ipAddr.GUELTIG_BIS > SYSDATE);


CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DEVICE_ROUTING FOR V_HURRICAN_DEVICE_ROUTING;

GRANT SELECT ON V_HURRICAN_DEVICE_ROUTING TO R_HURRICAN_BSI_VIEWS;

