-- Abfrage der Kunden pro OLT Port fuer ONTs und DPOs
-- (wird aus dem Service-Portal aufgerufen)

CREATE OR REPLACE VIEW V_KUNDE_PRO_OLTPORT AS
  SELECT
    ast.STATUS_TEXT     AS TECH_STATUS,
    ad.PRODAK_ORDER__NO AS TAIFUN_ORDER__NO,
    ad.VORGABE_SCV      AS PLANNED_START,
    ad.INBETRIEBNAHME   AS REAL_DATE,
    ad.KUENDIGUNG       AS CANCEL_DATE,
    p.ANSCHLUSSART,
    ad.PROD_ID,
    (CASE WHEN DPO_ONT.OLT_SUBRACK IS NULL
      THEN
        DPO_ONT.OLT_FRAME || '-' || DPO_ONT.OLT_SHELF || '-' || DPO_ONT.OLT_GPON_PORT || '-' || DPO_ONT.OLT_GPON_ID
     ELSE
       DPO_ONT.OLT_FRAME || '-' || DPO_ONT.OLT_SUBRACK || '-' || DPO_ONT.OLT_SHELF || '-' || DPO_ONT.OLT_GPON_PORT ||
       '-' || DPO_ONT.OLT_GPON_ID
     END
    )                   AS HW_EQN,
    RACK.GERAETEBEZ,
    t.TDN               AS VERBINDUNGSBEZEICHNUNG,
    ad.AUFTRAG_ID,
    EQUIPMENT.eq_id     AS PORT_ID,
    DPO_ONT.ONT_TYPE
  FROM T_HW_RACK RACK
    INNER JOIN (SELECT *
                FROM
                  (SELECT
                     dpo.RACK_ID,
                     dpo.OLT_RACK_ID,
                     dpo.DPO_TYPE AS ONT_TYPE,
                     dpo.OLT_SUBRACK,
                     dpo.OLT_FRAME,
                     dpo.OLT_SHELF,
                     dpo.OLT_GPON_PORT,
                     dpo.OLT_GPON_ID
                   FROM T_HW_RACK_DPO dpo)
                UNION
                (SELECT
                   ont.RACK_ID,
                   ont.OLT_RACK_ID,
                   ont.ONT_TYPE,
                   ont.OLT_SUBRACK,
                   ont.OLT_FRAME,
                   ont.OLT_SHELF,
                   ont.OLT_GPON_PORT,
                   ont.OLT_GPON_ID
                 FROM T_HW_RACK_ONT ont)) DPO_ONT
      ON (RACK.ID = DPO_ONT.OLT_RACK_ID)
    INNER JOIN T_HW_BAUGRUPPE BAUGRUPPE ON (DPO_ONT.RACK_ID = BAUGRUPPE.RACK_ID)
    INNER JOIN T_EQUIPMENT EQUIPMENT ON (Baugruppe.id = EQUIPMENT.HW_BAUGRUPPEN_ID)
    INNER JOIN T_RANGIERUNG RANGIERUNG ON (RANGIERUNG.EQ_IN_ID = EQUIPMENT.EQ_ID)
    INNER JOIN T_ENDSTELLE ENDSTELLE
      ON (ENDSTELLE.RANGIER_ID = RANGIERUNG.RANGIER_ID OR ENDSTELLE.RANGIER_ID_ADDITIONAL = RANGIERUNG.RANGIER_ID)
    INNER JOIN T_AUFTRAG_TECHNIK atech ON (atech.AT_2_ES_ID = ENDSTELLE.ES_GRUPPE)
    INNER JOIN T_AUFTRAG_DATEN ad ON (ad.AUFTRAG_ID = atech.AUFTRAG_ID)
    INNER JOIN T_AUFTRAG_STATUS ast ON (ad.STATUS_ID = ast.ID)
    INNER JOIN T_PRODUKT p ON (ad.PROD_ID = p.PROD_ID)
    LEFT JOIN T_TDN t ON (atech.TDN_ID = t.ID)
  WHERE RACK.RACK_TYP IN ('OLT', 'DSLAM')
        AND ad.GUELTIG_BIS > SYSDATE
        AND ad.STATUS_ID < 10000
        AND atech.GUELTIG_BIS > SYSDATE
        AND (ad.KUENDIGUNG IS NULL OR ad.KUENDIGUNG > SYSDATE)
        AND ad.INBETRIEBNAHME < SYSDATE;

GRANT SELECT ON V_KUNDE_PRO_OLTPORT TO HURRICAN_TECH_MUC;
