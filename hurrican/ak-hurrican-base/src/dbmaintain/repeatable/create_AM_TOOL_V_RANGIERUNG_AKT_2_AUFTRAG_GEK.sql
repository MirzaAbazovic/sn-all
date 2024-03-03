------------------------------------------------------------
-- KUP 330
-- create Basis-View für die AM Tool Abfrage
------------------------------------------------------------

create or replace view V_RANGIERUNG_AKT_2_AUFTRAG_GEK
AS
SELECT
       n.text AS NIEDERLASSUNG,
       h.onkz,
       h.asb,
       ad.bearbeiter,
       ad.prodak_order__no AS order__no,
       ad.auftrag_datum,
       ad.inbetriebnahme,
       ad.kuendigung,
       ad.vorgabe_scv,
       ad.bemerkungen,
       ad.bestellnr,
       e.id AS ENDSTELLE_ID,
       r.rangier_id,
       R.EQ_IN_ID,
       R.EQ_OUT_ID,
       R.HVT_ID_STANDORT,
       r.gueltig_von,
       r.GUELTIG_BIS,
       r.bemerkung
   FROM
     V1EDI_T_HVT_2_NL h,
     t_hvt_standort s,
     T_AUFTRAG_DATEN ad,
     T_AUFTRAG_TECHNIK at,
     T_ENDSTELLE e,
     T_RANGIERUNG r,
     T_NIEDERLASSUNG n
WHERE
      s.HVT_GRUPPE_ID = h.HVT_GRUPPE_ID and
      r.HVT_ID_STANDORT = s.HVT_ID_STANDORT and
      (AD.STATUS_ID >= 9000 OR AD.STATUS_ID = 3400 OR AD.STATUS_ID = 1150) AND
      r.gueltig_bis > SYSDATE AND
      AT.GUELTIG_BIS > SYSDATE AND
      AD.GUELTIG_BIS > SYSDATE AND
      AD.AUFTRAG_ID = AT.AUFTRAG_ID AND
      AT.AT_2_ES_ID = E.ES_GRUPPE AND
      E.RANGIER_ID = R.RANGIER_ID AND
      E.ID = R.ES_ID AND
      AT.NIEDERLASSUNG_ID = n.ID
order by
       n.text,
       h.onkz,
       h.asb,
       r.gueltig_von
/
commit
/
grant select on V_RANGIERUNG_AKT_2_AUFTRAG_GEK to R_HURRICAN_USER
/
