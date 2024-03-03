-- Abfrage der Kunden pro Baugruppe oder RACK
-- (wird aus dem Service-Portal aufgerufen)

CREATE OR REPLACE VIEW V_KUNDE_PRO_BAUGRUPPE as
 SELECT DISTINCT
    ast.STATUS_TEXT as TECH_STATUS,
    ad.PRODAK_ORDER__NO as TAIFUN_ORDER__NO,
    ad.VORGABE_SCV as PLANNED_START,
    ad.INBETRIEBNAHME as REAL_DATE,
    ad.KUENDIGUNG as CANCEL_DATE,
    p.ANSCHLUSSART,
    ad.PROD_ID,
	RACK.ID as RACK_ID,
	BAUGRUPPE.ID as BAUGRUPPE_ID,
   t.TDN           AS VERBINDUNGSBEZEICHNUNG,
   ad.AUFTRAG_ID,
   EQUIPMENT.eq_id AS PORT_ID
 from T_HW_RACK RACK
     inner join T_HW_BAUGRUPPE BAUGRUPPE  on (RACK.ID = BAUGRUPPE.RACK_ID)
     inner join T_EQUIPMENT EQUIPMENT     on (Baugruppe.id = EQUIPMENT.HW_BAUGRUPPEN_ID)
     inner join T_RANGIERUNG RANGIERUNG   on (RANGIERUNG.EQ_IN_ID = EQUIPMENT.EQ_ID)
     inner join T_ENDSTELLE ENDSTELLE     on (ENDSTELLE.RANGIER_ID=RANGIERUNG.RANGIER_ID or ENDSTELLE.RANGIER_ID_ADDITIONAL=RANGIERUNG.RANGIER_ID)
     inner join T_AUFTRAG_TECHNIK atech   on (atech.AT_2_ES_ID=ENDSTELLE.ES_GRUPPE)
     inner join T_AUFTRAG_DATEN ad        on (ad.AUFTRAG_ID=atech.AUFTRAG_ID)
     inner join T_AUFTRAG_STATUS ast      on (ad.STATUS_ID=ast.ID)
     inner join T_PRODUKT p               on (ad.PROD_ID=p.PROD_ID)
     left join T_TDN t                    on (atech.TDN_ID=t.ID)
 where
         ad.GUELTIG_BIS > SYSDATE
	 and ad.STATUS_ID < 10000
     and atech.GUELTIG_BIS > SYSDATE
     and (ad.KUENDIGUNG is null or ad.KUENDIGUNG > SYSDATE)
     and ad.INBETRIEBNAHME < SYSDATE;


GRANT SELECT ON V_KUNDE_PRO_BAUGRUPPE TO HURRICAN_TECH_MUC;
