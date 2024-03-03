-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2011 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung der ACLs zu einem Endgeraet.
--
-- Verwendete Tabellen:
--   + T_EG_ACL
--   + T_EG_CONFIG_2_ACL
--
-- Besonderheiten:
--

CREATE OR REPLACE FORCE VIEW V_HURRICAN_DEVICE_ACL
(
   DEVICE_CONFIG_ID,
   NAME,
   ROUTER_TYPE
)
AS
   SELECT ec2a.EG_CONFIG_ID AS DEVICE_CONFIG_ID,
        ea.NAME AS NAME,
        ea.ROUTERTYP AS ROUTER_TYPE
   FROM T_EG_ACL ea
        INNER JOIN T_EG_CONFIG_2_ACL ec2a on ea.ID=ec2a.EG_ACL_ID;


CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DEVICE_ACL FOR V_HURRICAN_DEVICE_ACL;

GRANT SELECT ON V_HURRICAN_DEVICE_ACL TO R_HURRICAN_BSI_VIEWS;

