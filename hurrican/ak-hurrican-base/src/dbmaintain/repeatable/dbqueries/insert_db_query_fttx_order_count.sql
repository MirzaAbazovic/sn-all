BEGIN
    execute immediate 'drop type FTTX_ORDER_COUNT_TABLE';
 EXCEPTION
    WHEN OTHERS THEN
 NULL;
 END;
/

-- eigenen Datentyp erstellen, ueber den die Results gespeichert werden
CREATE OR REPLACE TYPE FTTX_ORDER_COUNT_TYPE
AS OBJECT
(
  ACTIVE_ORDER_COUNT   INT,
  CLUSTER_TYP VARCHAR2(10),
  CLUSTER_ID VARCHAR2(100),
  PRODUKT VARCHAR2(100),
  DOWNSTREAM INT
);
/

-- Table-Objekt aus definiertem Datentyp erstellen
CREATE OR REPLACE TYPE FTTX_ORDER_COUNT_TABLE AS TABLE OF FTTX_ORDER_COUNT_TYPE
/

-- Function erstellen, die die Select-Anweisungen (inkl. Parameter) enthaelt und
-- die Ergebnisse in die oben erstellte Tabelle eintraegt
create or replace function F_FTTX_COUNTS (check_date IN DATE) RETURN FTTX_ORDER_COUNT_TABLE PIPELINED IS
BEGIN
  for rec in (
     select count(*) as ACTIVE_ORDERS, 'FTTB/H' as CLUSTER_TYP, HVT.CLUSTER_ID, P.ANSCHLUSSART as PRODUKT, tls.LONG_VALUE as DOWNSTREAM
         from t_auftrag_daten ad
         inner join t_auftrag_technik atech on ad.auftrag_id=atech.auftrag_id
         inner join t_endstelle es on ATECH.AT_2_ES_ID=ES.ES_GRUPPE
         inner join T_HVT_STANDORT hvt on ES.HVT_ID_STANDORT=HVT.HVT_ID_STANDORT
         inner join T_PRODUKT p on AD.PROD_ID=P.PROD_ID
         left join T_AUFTRAG_2_TECH_LS atls on ad.auftrag_id=atls.auftrag_id
         left join T_TECH_LEISTUNG tls on ATLS.TECH_LS_ID=tls.ID
         where
           ad.gueltig_bis>SYSDATE
           and atech.gueltig_bis>SYSDATE
           and ((ad.status_id in (6000,6100,6200) and ad.inbetriebnahme<=check_date) or (ad.status_id >= 9000 and ad.status_id<9800 and AD.KUENDIGUNG>check_date))
           and HVT.CLUSTER_ID is not null
           and (P.PROD_ID in (512,513,540,541) and (TLS.TYP is null or (tls.TYP='DOWNSTREAM' and (atls.aktiv_bis>check_date or atls.aktiv_bis is null)))
             or
               p.prod_id in (511)
             )
           group by HVT.CLUSTER_ID, p.ANSCHLUSSART, tls.LONG_VALUE
    UNION ALL
    select count(*) as ACTIVE_ORDERS, 'FTTC' as CLUSTER_TYP, HVTG.KOSTENSTELLE, P.ANSCHLUSSART as PRODUKT, tls.LONG_VALUE as DOWNSTREAM
         from t_auftrag_daten ad
         inner join t_auftrag_technik atech on ad.auftrag_id=atech.auftrag_id
         inner join t_endstelle es on ATECH.AT_2_ES_ID=ES.ES_GRUPPE
         inner join T_HVT_STANDORT hvt on ES.HVT_ID_STANDORT=HVT.HVT_ID_STANDORT
         inner join T_HVT_GRUPPE hvtg on HVT.HVT_GRUPPE_ID=hvtg.HVT_GRUPPE_ID
         inner join T_PRODUKT p on AD.PROD_ID=P.PROD_ID
         left join T_AUFTRAG_2_TECH_LS atls on ad.auftrag_id=atls.auftrag_id
         left join T_TECH_LEISTUNG tls on ATLS.TECH_LS_ID=tls.ID
         where
           ad.gueltig_bis>SYSDATE
           and atech.gueltig_bis>SYSDATE
           and ((ad.status_id in (6000,6100,6200) and ad.inbetriebnahme<=check_date) or (ad.status_id >= 9000 and ad.status_id<9800 and AD.KUENDIGUNG>check_date))
           and HVT.BETRIEBSRAUM_ID is not null
           and HVT.STANDORT_TYP_REF_ID=11013
              and (P.PROD_ID in (512,513,540,541) and (TLS.TYP is null or (tls.TYP='DOWNSTREAM' and (atls.aktiv_bis>check_date or atls.aktiv_bis is null)))
             or
               p.prod_id in (511)
             )
           group by HVTG.KOSTENSTELLE, p.ANSCHLUSSART, tls.LONG_VALUE
    UNION ALL
    select count(*) as ACTIVE_ORDERS, 'VDSL_HVT' as CLUSTER_TYP, HVTG.KOSTENSTELLE, P.ANSCHLUSSART as PRODUKT, tls.LONG_VALUE as DOWNSTREAM
         from t_auftrag_daten ad
         inner join t_auftrag_technik atech on ad.auftrag_id=atech.auftrag_id
         inner join t_endstelle es on ATECH.AT_2_ES_ID=ES.ES_GRUPPE
         inner join T_HVT_STANDORT hvt on ES.HVT_ID_STANDORT=HVT.HVT_ID_STANDORT
         inner join T_HVT_GRUPPE hvtg on HVT.HVT_GRUPPE_ID=hvtg.HVT_GRUPPE_ID
         inner join T_PRODUKT p on AD.PROD_ID=P.PROD_ID
         left join T_AUFTRAG_2_TECH_LS atls on ad.auftrag_id=atls.auftrag_id
         left join T_TECH_LEISTUNG tls on ATLS.TECH_LS_ID=tls.ID
         where
           ad.gueltig_bis>SYSDATE
           and atech.gueltig_bis>SYSDATE
           and ((ad.status_id in (6000,6100,6200) and ad.inbetriebnahme<=check_date) or (ad.status_id >= 9000 and ad.status_id<9800 and AD.KUENDIGUNG>check_date))
           and HVT.STANDORT_TYP_REF_ID=11000
              and (P.PROD_ID in (512,513,514,515,540,541) and (TLS.TYP is null or (tls.TYP='DOWNSTREAM' and (atls.aktiv_bis>check_date or atls.aktiv_bis is null)))
             or
               p.prod_id in (511)
             )
           group by HVTG.KOSTENSTELLE, p.ANSCHLUSSART, tls.LONG_VALUE
  ) loop
     pipe row (FTTX_ORDER_COUNT_TYPE(rec.ACTIVE_ORDERS, rec.CLUSTER_TYP, rec.CLUSTER_ID, rec.PRODUKT, rec.DOWNSTREAM));
  end loop;
END;
/

grant EXECUTE on F_FTTX_COUNTS to R_HURRICAN_USER;
grant EXECUTE on F_FTTX_COUNTS to R_HURRICAN_READ_ONLY;

delete from t_db_queries where id=200;
Insert into T_DB_QUERIES
   (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY, PARAMS, VERSION)
 Values
   (200, 'FTTX_Auftragszahlen', 'Auftragszahlen pro FTTX Cluster u. Produkt/Bandbreite', 'CC',
   'SELECT * FROM TABLE(F_FTTX_COUNTS(?)) where ''controlling''=? order by cluster_typ, cluster_id, produkt, downstream', 'datum=date,Passwort=string', 0);


