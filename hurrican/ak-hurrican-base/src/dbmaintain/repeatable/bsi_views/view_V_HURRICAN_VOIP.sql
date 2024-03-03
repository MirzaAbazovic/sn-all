-- +++++++++++++++++++++++++++++++++++++++++++++++++++
-- Copyright (c) 2008 - M-net Telekommunikations GmbH
-- All rights reserved.
-- +++++++++++++++++++++++++++++++++++++++++++++++++++
--
-- View fuer die Darstellung der VoIP-Option zu einem
-- technischen Auftrag.
--
-- Verwendete Tabellen:
--   + T_AUFTRAG_VOIP
--   + T_REFERENCE
--
-- Besonderheiten:
--   + Es wird nur der aktuelle VoIP-Datensatz beruecksichtigt!
--   + Gibt es mind. einen DN_PLAN, wird nur der aktuelle beruecksichtigt. Existieren nur DN Plaene fuer die
--     Zukunft, so wird der als naechstes faellige zu heute gewaehlt.
--


CREATE OR REPLACE FORCE VIEW V_HURRICAN_VOIP
AS
  SELECT
    v.AUFTRAG_ID         AS TECH_ORDER_ID,
    dn.DN__NO            AS TAIFUN_DN__NO,
    dn.SIP_PASSWORD      AS SIP_PASSWORD,
    r.ID                 AS EG_MODE_ID,
    r.STR_VALUE          AS EG_MODE,
    v.IS_ACTIVE          AS IS_ACTIVE,
    r2.STR_VALUE         AS SIP_DOMAIN,
    p.SIP_LOGIN          AS SIP_LOGIN,
    p.SIP_HAUPTRUFNUMMER AS SIP_HAUPTRUFNUMMER
  FROM
    T_AUFTRAG_VOIP v
    INNER JOIN T_AUFTRAG_VOIP_DN dn ON v.AUFTRAG_ID = dn.AUFTRAG_ID
    LEFT JOIN T_AUFTRAG_VOIP_DN_PLAN p ON dn.ID = p.AUFTRAG_VOIP_DN_ID
    LEFT JOIN T_REFERENCE r ON v.EG_MODE = r.ID
    LEFT JOIN T_REFERENCE r2 ON dn.SIP_DOMAIN_REF_ID = r2.ID
  WHERE
    v.GUELTIG_BIS > SYSDATE
    AND ((SELECT count(*)
          FROM T_AUFTRAG_VOIP_DN_PLAN
          WHERE AUFTRAG_VOIP_DN_ID = dn.ID) <= 0
         OR NVL((SELECT max(p2.GUELTIG_AB)
                 FROM
                   T_AUFTRAG_VOIP_DN_PLAN p2
                 WHERE
                   p2.AUFTRAG_VOIP_DN_ID = dn.ID
                   AND p2.GUELTIG_AB <= SYSDATE),
                (SELECT min(p2.GUELTIG_AB)
                 FROM
                   T_AUFTRAG_VOIP_DN_PLAN p2
                 WHERE
                   p2.AUFTRAG_VOIP_DN_ID = dn.ID
                   AND p2.GUELTIG_AB > SYSDATE)) = p.GUELTIG_AB);

CREATE OR REPLACE SYNONYM BSICRM.V_HURRICAN_VOIP FOR V_HURRICAN_VOIP;

GRANT SELECT ON V_HURRICAN_VOIP TO R_HURRICAN_BSI_VIEWS;
