-- Abfrage der Kunden pro OLT Port f�r MDU
-- (wird aus dem Service-Portal aufgerufen)

CREATE OR REPLACE VIEW V_KUNDE_PRO_INT_ACCOUNT AS
  SELECT DISTINCT
    ast.STATUS_TEXT     AS TECH_STATUS,
    ad.PRODAK_ORDER__NO AS TAIFUN_ORDER__NO,
    ad.VORGABE_SCV      AS PLANNED_START,
    ad.INBETRIEBNAHME   AS REAL_DATE,
    ad.KUENDIGUNG       AS CANCEL_DATE,
    p.ANSCHLUSSART,
    ad.PROD_ID,
    intacc.account,
    intacc.passwort,
    intacc.gesperrt,
    t.TDN               AS VERBINDUNGSBEZEICHNUNG,
    ad.AUFTRAG_ID,
    EQUIPMENT.eq_id     AS PORT_ID
  FROM T_HW_RACK RACK
    INNER JOIN T_HW_BAUGRUPPE BAUGRUPPE ON (RACK.ID = BAUGRUPPE.RACK_ID)
    INNER JOIN T_EQUIPMENT EQUIPMENT ON (Baugruppe.id = EQUIPMENT.HW_BAUGRUPPEN_ID)
    INNER JOIN T_RANGIERUNG RANGIERUNG ON (RANGIERUNG.EQ_IN_ID = EQUIPMENT.EQ_ID)
    INNER JOIN T_ENDSTELLE ENDSTELLE
      ON (ENDSTELLE.RANGIER_ID = RANGIERUNG.RANGIER_ID OR ENDSTELLE.RANGIER_ID_ADDITIONAL = RANGIERUNG.RANGIER_ID)
    INNER JOIN T_AUFTRAG_TECHNIK atech ON (atech.AT_2_ES_ID = ENDSTELLE.ES_GRUPPE)
    INNER JOIN T_INT_ACCOUNT intacc ON (atech.INT_ACCOUNT_ID = intacc.ID)
    INNER JOIN T_AUFTRAG_DATEN ad ON (ad.AUFTRAG_ID = atech.AUFTRAG_ID)
    INNER JOIN T_AUFTRAG_STATUS ast ON (ad.STATUS_ID = ast.ID)
    INNER JOIN T_PRODUKT p ON (ad.PROD_ID = p.PROD_ID)
    LEFT JOIN T_TDN t ON (atech.TDN_ID = t.ID)
  WHERE
    ad.GUELTIG_BIS > SYSDATE
    AND ad.STATUS_ID < 10000
    AND atech.GUELTIG_BIS > SYSDATE
    AND (ad.KUENDIGUNG IS NULL OR ad.KUENDIGUNG > SYSDATE)
    AND ad.INBETRIEBNAHME < SYSDATE
    AND intacc.gueltig_bis > SYSDATE;

GRANT SELECT ON V_KUNDE_PRO_INT_ACCOUNT TO HURRICAN_TECH_MUC;