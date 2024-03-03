--
-- GRANTs fuer die DB-Rolle 'R_HURRICAN_BSI_VIEWS'
-- (Rolle besitzt nur Lese-Rechte auf die fuer BSI CRM erstellte DB-Views.)
--

CREATE ROLE "R_HURRICAN_BSI_VIEWS" NOT IDENTIFIED;

GRANT SELECT ON V_HURRICAN_ACCESSPOINT TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_ACCOUNTS TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_AQS TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_CUSTOMER_LOCKS TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_CARRIER_CONTACT TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_CARRIER_KENNUNG TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_CARRIER_MAPPING TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_DEVICE TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_DEVICE_CONFIG TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_DEVICE_CFG_PORT TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_DN_SERVICES TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_DSLAM_PROFILE TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_PHYSIC TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_PHYSIC_LIST TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_TAL_ORDER_DETAIL TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_TAL_ORDER TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_TECH_ORDER_BASE TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_TECH_PROCESS_HIST TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_TECH_SERVICES TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_IP TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_QOS TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_VOIP TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_ADDRESS TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_PHYSIC_HISTORY TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_CONTACT_PERSON TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_SERVICE_PROVIDER TO R_HURRICAN_BSI_VIEWS;
GRANT SELECT ON V_HURRICAN_GEO_2_ASB TO R_HURRICAN_BSI_VIEWS;

commit;



