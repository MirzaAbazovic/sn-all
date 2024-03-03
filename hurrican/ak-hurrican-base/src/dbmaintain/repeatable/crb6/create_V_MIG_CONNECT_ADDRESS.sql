-- SQL Skript, um die Basis-View fuer die (Connect) Adress-Migration zu erstellen

create or replace force view V_MIG_CONNECT_ADDRESS
as
  select AD.AUFTRAG_ID, AD.PRODAK_ORDER__NO, AD.STATUS_ID, E.ID as ES_ID, E.ES_TYP as ES_TYP,
   e.ENDSTELLE, e.NAME, e.ORT, e.PLZ, e.SL_ID, E.ADDRESS_ID, ATECH.NIEDERLASSUNG_ID
   from T_ENDSTELLE e
   inner join T_AUFTRAG_TECHNIK atech on E.ES_GRUPPE=ATECH.AT_2_ES_ID
   inner join T_AUFTRAG_DATEN ad on ATECH.AUFTRAG_ID=AD.AUFTRAG_ID
   inner join T_PRODUKT prod on AD.PROD_ID=PROD.PROD_ID
   inner join T_PRODUKTGRUPPE pg on PROD.PRODUKTGRUPPE_ID=PG.ID
   where
    PG.ID in (2,7,8,9)
    and ATECH.GUELTIG_BIS>SYSDATE
    and AD.GUELTIG_BIS>SYSDATE
    and E.ADDRESS_ID is null and E.ORT is not null
    and ad.status_id not in (1150,3400) and ad.STATUS_ID>=4000 and AD.STATUS_ID<=9800
;

