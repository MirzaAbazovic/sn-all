delete from T_DB_QUERIES where ID=17;

Insert into T_DB_QUERIES
   (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY,
    PARAMS, VERSION)
 Values
   (17, 'Online Gutschrift Händler',
   'Aufträge von Händler online bestellt ohne Gutschriftsleistung',
   'BILLING',
   'select CUSTOMER.CUST_NO as HAENDLER_NO, CUSTOMER.NAME as HAENDLER_NAME, CUSTOMER.FIRSTNAME as HAENDLER_VORNAME,
  AUFTRAG.VALID_FROM as AUFTRAG_GUELTIG_VON, AUFTRAG.AUFTRAG__NO as AUFTRAG__NO, AUFTRAG.CUST_NO as KUNDEN__NO, PRODUKT.NAME as PRODUKT
  from AUFTRAG auftrag
  inner join CUSTOMER customer on AUFTRAG.HAENDLER__NO=CUSTOMER.CUST_NO
  inner join OE produkt on AUFTRAG.OE__NO=PRODUKT.OE__NO
  where
  AUFTRAG.BESTELL_ID is not null
  and (AUFTRAG.HAENDLER__NO is not null or AUFTRAG.EXT_PORTAL_ID is not null)
  and CUSTOMER.FIRSTNAME not like ''M-net Maxi Shop''
  and AUFTRAG.VALID_FROM between ? and ?
  and AUFTRAG.VALID_TO=to_date(''31.12.9999'', ''dd.mm.yyyy'')
  and PRODUKT.VALID_TO=to_date(''31.12.9999'', ''dd.mm.yyyy'')
  and AUFTRAG.HIST_STATUS not in (''UNG'', ''ALT'')
  and AUFTRAG.AUFTRAG__NO not in (
    select AUFTRAGTMP.AUFTRAG__NO
      from AUFTRAG auftragtmp
      inner join CUSTOMER customertmp on AUFTRAGTMP.HAENDLER__NO=CUSTOMERTMP.CUST_NO
      left join AUFTRAGPOS auftragpos on AUFTRAGTMP.AUFTRAG__NO=AUFTRAGPOS.ORDER__NO
      left join LEISTUNG leistung on AUFTRAGPOS.SERVICE_ELEM__NO=LEISTUNG.LEISTUNG__NO
      where
        AUFTRAG.BESTELL_ID is not null
        and AUFTRAG.HAENDLER__NO is not null
        and CUSTOMER.FIRSTNAME not like ''M-net Maxi Shop''
        and AUFTRAG.HIST_STATUS not in (''UNG'', ''ALT'')
        and LEISTUNG.VALID_TO=to_date(''31.12.9999'', ''dd.mm.yyyy'')
        and LEISTUNG.NAME=''G/Online-Bestellung Händler''
  )',
  'von=date,bis=date', 0);