-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der aktuellen Peering Partnern mit zugehoerigen IP-Adressen.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_2_PEERING_PARTNER
--   + T_SIP_PEERING_PARTNER
--   + T_SIP_SBC_IP_SET
--   + T_SIP_SBC_IP
--   + T_IP_ADDRESS
--
-- Besonderheiten:
--

CREATE or REPLACE FORCE VIEW V_HURRICAN_SIP_PP_IPS
  AS SELECT
    a2pp.AUFTRAG_ID as AUFTRAG_ID,
    pp.NAME as PEERING_PARTNER_NAME,
    a2pp.GUELTIG_VON as GUELTIG_VON,
    a2pp.GUELTIG_BIS as GUELTIG_BIS,
    ip.ADDRESS AS IP_ADDRESS,
    ip.ADDRESS_TYPE as ADDRESS_TYPE
  FROM
    T_AUFTRAG_2_PEERING_PARTNER a2pp
    INNER JOIN T_SIP_PEERING_PARTNER pp ON (a2pp.PEERING_PARTNER_ID = pp.ID
              AND a2pp.GUELTIG_VON <= SYSDATE AND a2pp.GUELTIG_BIS > SYSDATE)
    INNER JOIN T_SIP_SBC_IP_SET sbcIpSet ON
        (sbcIpSet.SIP_PP_ID = pp.ID AND sbcIpSet.GUELTIG_AB =
                      (SELECT max(GUELTIG_AB) FROM T_SIP_SBC_IP_SET s
                        WHERE s.SIP_PP_ID = sbcIpSet.SIP_PP_ID AND s.GUELTIG_AB <= SYSDATE
                        GROUP BY s.SIP_PP_ID
                      )
        )
    INNER JOIN T_SIP_SBC_IP sbcIp ON sbcIp.SIP_SBC_IP_SET_ID = sbcIpSet.ID
    INNER JOIN T_IP_ADDRESS ip ON ip.ID = sbcIp.IP_ADDRESS_ID;

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_SIP_PP_IPS FOR V_HURRICAN_SIP_PP_IPS;

GRANT SELECT ON V_HURRICAN_SIP_PP_IPS TO R_HURRICAN_BSI_VIEWS;
