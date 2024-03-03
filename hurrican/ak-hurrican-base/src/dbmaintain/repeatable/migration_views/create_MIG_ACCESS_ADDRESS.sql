create or replace force VIEW MIGRATION_ACCESS_ADDRESS as
  select ad.AUFTRAG_ID as AUFTRAG_ID, ad.PRODAK_ORDER__NO as AUFTRAG__NO, e.ID as ENDSTELLE_ID,
    e.ES_TYP as ENDSTELLE_TYP
   from t_endstelle e
     inner join T_ANSPRECHPARTNER a on E.ADDRESS_ID=A.ADDRESS_ID
     inner join T_AUFTRAG_TECHNIK atech on E.ES_GRUPPE=ATECH.AT_2_ES_ID
     inner join T_AUFTRAG_DATEN ad on atech.AUFTRAG_ID=ad.AUFTRAG_ID
     inner join t_address addr on e.address_id=addr.id
   where ATECH.GUELTIG_BIS>sysdate
     and ad.GUELTIG_BIS>sysdate
   order by ad.AUFTRAG_ID asc
;

GRANT SELECT ON MIGRATION_ACCESS_ADDRESS TO R_HURRICAN_USER;
