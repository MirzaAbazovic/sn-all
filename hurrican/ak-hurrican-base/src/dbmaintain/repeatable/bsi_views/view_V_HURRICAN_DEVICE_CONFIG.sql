-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View zur Darstellung der Konfiguration eines Endgeraets.
--
-- Verwendete Tabellen:
--   + T_EG_CONFIG
--
-- Besonderheiten:
--   + Es wird immer nur die aktuelle Konfiguration des EGs ermittelt
--

CREATE OR REPLACE FORCE VIEW V_HURRICAN_DEVICE_CONFIG
(
   TECH_ORDER_DEVICE_ID,
   DEVICE_CONFIG_ID,
   PRODUCER,
   MODEL,
   SERIAL_NUMBER,
   NAT,
   IP_LAN,
   SUBNETMASK, 
   DHCP,
   COUNT_IP,
   DEVICE_COUNT_ACT,
   DEVICE_COUNT_1YEAR,
   DEVICE_COUNT_2YEARS,
   IP_VIRTUAL_WEB,
   CONTACT_ID_ADMIN,
   CONTACT_ID_TECHNICAL,
   DEVICE_USERNAME,
   DEVICE_PASSWORD,
   TRIGGERPOINT,
   CALLED_STATION_ID,
   CALLING_STATION_ID,
   INTERVALL,
   RE_ENABLE,
   IDLE_TIMER,
   ATTEMPTS,
   FREQUENCY,
   CHANNEL_GROUPING,
   DESCRIPTION,
   DHCP_POOL_FROM,
   DHCP_POOL_TO,
   QOS_ACTIVE,
   IP_WAN_CONST,
   WAN_VP,
   WAN_VC,
   DNS_SRV_IP,
   DNS_SRV_SUBNET,
   MTU,
   SOFTWARESTAND
)
AS
   SELECT   ec.EG2A_ID AS TECH_ORDER_DEVICE_ID,
            ec.ID AS DEVICE_CONFIG_ID,
            (case WHEN egtype.HERSTELLER IS NULL THEN ec.HERSTELLER ELSE egtype.HERSTELLER end) AS PRODUCER,
            (case WHEN egtype.MODELL IS NULL THEN ec.MODELL ELSE egtype.MODELL end) AS MODEL,
            ec.SERIAL_NUMBER AS SERIAL_NUMBER,
            ec.NAT AS NAT,
            (SELECT   ipAddr.ADDRESS
               FROM   T_EG_IP a
                 LEFT JOIN T_IP_ADDRESS ipAddr ON a.IP_ADDRESS_ID = ipAddr.id
              WHERE       ROWNUM = 1
                      AND A.EG2A_ID = ec.EG2A_ID
                      AND a.ADDRESS_TYPE = 'LAN')
               AS IP_LAN,
            NULL AS SUBNETMASK,
            ec.DHCP AS DHCP,
            ec.ANZAHL_IP AS COUNT_IP,
            CAST (NULL AS NUMBER) AS DEVICE_COUNT_ACT,
            CAST (NULL AS NUMBER) AS DEVICE_COUNT_1YEAR,
            CAST (NULL AS NUMBER) AS DEVICE_COUNT_2YEARS,
            CAST (NULL AS NUMBER) AS IP_VIRTUAL_WEB,
            CAST (NULL AS NUMBER) AS CONTACT_ID_ADMIN,
            CAST (NULL AS NUMBER) AS CONTACT_ID_TECHNICAL,
            ec.EG_USER AS DEVICE_USERNAME,
            ec.EG_PASSWORD AS DEVICE_PASSWORD,
            tpIp.ADDRESS AS TRIGGERPOINT,
            ec.CALLED_STATION_ID AS CALLED_STATION_ID,
            ec.CALLING_STATION_ID AS CALLING_STATION_ID,
            ec.INTERVALL AS INTERVALL,
            ec.RE_ENABLE AS RE_ENABLE,
            ec.IDLE_TIMER AS IDLE_TIMER,
            ec.ATTEMPS AS ATTEMPTS,
            ec.FREQUENCY AS FREQUENCY,
            ec.KANALBUENDELUNG AS CHANNEL_GROUPING,
            ec.BEMERKUNG AS DESCRIPTION,
            dpfIp.ADDRESS AS DHCP_POOL_FROM,
            dptIp.ADDRESS AS DHCP_POOL_TO,
            ec.QOS_ACTIVE AS QOS_ACTIVE,
            ec.WAN_IP_FEST AS IP_WAN_CONST,
            ec.WANVP AS WAN_VP,
            ec.WANVC AS WAN_VC,
            ec.DNS_SERVER_IP AS DNS_SRV_IP,
            NULL AS DNS_SRV_SUBNET,
            ec.MTU AS MTU,
            ec.SW_STAND AS SOFTWARESTAND

     FROM   T_EG_CONFIG ec
       LEFT JOIN T_IP_ADDRESS tpIp ON (ec.TRIGGERPUNKT_ID = tpIp.id
            AND tpIp.GUELTIG_VON <= SYSDATE AND tpIp.GUELTIG_BIS > SYSDATE)
       LEFT JOIN T_IP_ADDRESS dpfIp ON (ec.DHCP_POOL_FROM_ID = dpfIp.id
            AND dpfIp.GUELTIG_VON <= SYSDATE AND dpfIp.GUELTIG_BIS > SYSDATE)
       LEFT JOIN T_IP_ADDRESS dptIp ON (ec.DHCP_POOL_FROM_ID = dptIp.id
            AND dptIp.GUELTIG_VON <= SYSDATE AND dptIp.GUELTIG_BIS > SYSDATE)
       LEFT JOIN T_EG_TYPE egtype ON (ec.EG_TYPE_ID = egtype.ID)
       ;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_DEVICE_CONFIG FOR V_HURRICAN_DEVICE_CONFIG;

GRANT SELECT ON V_HURRICAN_DEVICE_CONFIG TO R_HURRICAN_BSI_VIEWS;

