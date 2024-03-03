--
-- nachtraegliche Konfiguration der angelegten Taifun-Leistungen
--

-- Updates in HURRICAN
update t_produkt set PRODUKT_NR=2157 where PROD_ID=450;  -- Connect
update t_produkt set PRODUKT_NR=2158 where PROD_ID=451;  -- Connect-LAN
update t_produkt set PRODUKT_NR=2162 where PROD_ID=452;  -- MetroEthernet
update t_produkt set PRODUKT_NR=2160 where PROD_ID=453;  -- DarkFiber
update t_produkt set PRODUKT_NR=2159 where PROD_ID=454;  -- DarkCopper
update t_produkt set PRODUKT_NR=2161 where PROD_ID=455;  -- DirectAccess


-- Updates in TAIFUN
-- Connect
update leistung set EXT_PRODUKT__NO='450' where OE__NO=2157 and LEISTUNGKAT like 'WIEDERHOLT%'; 
-- Connect-LAN
update leistung set EXT_PRODUKT__NO='451' where OE__NO=2158 and LEISTUNGKAT like 'WIEDERHOLT%'; 
-- MetroEthernet
update leistung set EXT_PRODUKT__NO='452' where OE__NO=2162 and LEISTUNGKAT like 'WIEDERHOLT%'; 
-- DarkFiber
update leistung set EXT_PRODUKT__NO='453' where OE__NO=2160 and LEISTUNGKAT like 'WIEDERHOLT%'; 
-- DarkCopper
update leistung set EXT_PRODUKT__NO='454' where OE__NO=2159 and LEISTUNGKAT like 'WIEDERHOLT%'; 
-- DirectAccess
update leistung set EXT_PRODUKT__NO='455' where OE__NO=2161 and LEISTUNGKAT like 'WIEDERHOLT%'; 


-- Bandbreiten-Leistungen definieren (Mapping in Taifun-Leistung auf technische Leistung generieren)
update leistung set EXT_LEISTUNG__NO=30100 where NAME='< 2 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30144 where NAME='DC' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30145 where NAME='DF' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30137 where NAME='DWDM' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30138 where NAME='FibreChannel, 1 Gbit/s, Multimode' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30139 where NAME='FibreChannel, 1 Gbit/s, Singlemode' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30140 where NAME='FibreChannel, 2 Gbit/s, Multimode' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30141 where NAME='FibreChannel, 2 Gbit/s, Singlemode' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30142 where NAME='FibreChannel, 4 Gbit/s, Multimode' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30143 where NAME='FibreChannel, 4 Gbit/s, Singlemode' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30117 where NAME='10BASE-T, 10Mbit/s, unmanaged' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30112 where NAME='100BASE-X, 10 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30113 where NAME='100BASE-X, 100 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30114 where NAME='100BASE-X, 100Mbit/s, unmanaged' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30115 where NAME='100BASE-X, 25 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30116 where NAME='100BASE-X, 50 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30107 where NAME='1000BASE-X, 100 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30108 where NAME='1000BASE-X, 1000 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30109 where NAME='1000BASE-X, 1000Mbit/s, unmanaged' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30110 where NAME='1000BASE-X, 250 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30111 where NAME='1000BASE-X, 500 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30106 where NAME='10/100 BASE-T, 6Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30102 where NAME='10/100BASE-T, 10Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30103 where NAME='10/100BASE-T, 2Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30104 where NAME='10/100BASE-T, 4Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30105 where NAME='10/100BASE-T, 8Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30118 where NAME like '155 Mbit/s (STM-1), fraktioniert' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30119 where NAME='155 Mbit/s (STM-1), G.703' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30120 where NAME='155 Mbit/s (STM-1), G.957' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30121 where NAME='2 Mbit/s, G.703' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30122 where NAME='2 Mbit/s, G.704' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30123 where NAME='2 Mbit/s, X.21' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30124 where NAME='2500 Mbit/s (STM-16)' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30125 where NAME='2500 Mbit/s (STM-16) concatenation' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30126 where NAME='2500 Mbit/s (STM-16), fractional 155Mbit' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30127 where NAME='2500 Mbit/s (STM-16), fractional 622Mbit' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30128 where NAME like '2500 Mbit/s (STM-16), fraktioniert' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30129 where NAME='34 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30130 where NAME='34 Mbit/s, fractional (2Mbit/s)' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30131 where NAME='45 Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30132 where NAME='45 Mbit/s, fractional (2Mbit/s)' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30133 where NAME='622 Mbit/s (STM-4)' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30134 where NAME='622 Mbit/s (STM-4) concatenation' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30135 where NAME='622 Mbit/s (STM-4), fractional 155Mbit/s' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30136 where NAME='622 Mbit/s, (STM-4), fraktioniert' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30146 where NAME like 'Direct-Access 2 M%' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30147 where NAME like 'Direct-Access 4 M%' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30148 where NAME like 'Direct-Access 6 M%' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30149 where NAME like 'Direct-Access 8 M%' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30150 where NAME like 'Direct-Access 10 M%' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30151 where NAME like 'Direct-Access 34 M%' and OE__NO>=2157;
update leistung set EXT_LEISTUNG__NO=30152 where NAME like 'Direct-Access 100 M%' and OE__NO>=2157;


-- Leistung "Zusatzleitung" fuer die einzelnen Produkte anlegen
alter session set nls_date_format='yyyy-mm-dd';
insert into LEISTUNG (LEISTUNG_NO, LEISTUNG__NO, GUELTIG_VON, GUELTIG_BIS, HIST_STATUS, HIST_CNT, HIST_LAST,
  PRIMARY_INSTANCE, GENERATE_BILLPOS, NAME, TECH_EXPORT, DISABLE_NEW, OE__NO, LER_NO, LEISTUNGKAT, CORRECTION,
  RF_FEE_PERIOD, RF_DUE_PERIOD, RF_ALIGNED_PERIOD, RF_PRO_RATA, RF_PAYBACK, LEISTUNGTYP, PRICE_SCHEME, USER_PRICE_SCHEME,
  SEPARATED_BILLPOS, PROPORTIONAL, PREIS_EDITIERBAR, PREIS, WAEHRUNG_ID, VAT_INCLUDED, MENGE_EDITIERBAR,
  MENGE, MASS, MWST_SATZ, BILLING_PRICE_SOURCE, BILLING_QUANTITY_SOURCE, BS_RELEVANT, USERW, DATEW, EXT_PRODUKT__NO,
  MAY_OVERLAP)
  values 
  (40830, 40830, '1996-01-01', '9999-12-31', 'AKT', 0, 1,
  1, 0, 'Zusatzleitung', 1, 0, 2157, 1, 'WIEDERHOLT     ', 0, 
  1, 0, 1, 1, 0, 'OPTIONAL        ', 'FIX', 'FIX', 
  0, 0, 0, 0, 'EUR', 0, 1, 
  1, 'Stck    ', 19, 'service_element', 'service_element', 0, 'MNETTEST  ', '2008-01-20', '456',
  0);
insert into LEISTUNG (LEISTUNG_NO, LEISTUNG__NO, GUELTIG_VON, GUELTIG_BIS, HIST_STATUS, HIST_CNT, HIST_LAST,
  PRIMARY_INSTANCE, GENERATE_BILLPOS, NAME, TECH_EXPORT, DISABLE_NEW, OE__NO, LER_NO, LEISTUNGKAT, CORRECTION,
  RF_FEE_PERIOD, RF_DUE_PERIOD, RF_ALIGNED_PERIOD, RF_PRO_RATA, RF_PAYBACK, LEISTUNGTYP, PRICE_SCHEME, USER_PRICE_SCHEME,
  SEPARATED_BILLPOS, PROPORTIONAL, PREIS_EDITIERBAR, PREIS, WAEHRUNG_ID, VAT_INCLUDED, MENGE_EDITIERBAR,
  MENGE, MASS, MWST_SATZ, BILLING_PRICE_SOURCE, BILLING_QUANTITY_SOURCE, BS_RELEVANT, USERW, DATEW, EXT_PRODUKT__NO,
  MAY_OVERLAP)
  values 
  (40831, 40831, '1996-01-01', '9999-12-31', 'AKT', 0, 1,
  1, 0, 'Zusatzleitung', 1, 0, 2158, 1, 'WIEDERHOLT     ', 0, 
  1, 0, 1, 1, 0, 'OPTIONAL        ', 'FIX', 'FIX', 
  0, 0, 0, 0, 'EUR', 0, 1, 
  1, 'Stck    ', 19, 'service_element', 'service_element', 0, 'MNETTEST  ', '2008-01-20', '456',
  0);
insert into LEISTUNG (LEISTUNG_NO, LEISTUNG__NO, GUELTIG_VON, GUELTIG_BIS, HIST_STATUS, HIST_CNT, HIST_LAST,
  PRIMARY_INSTANCE, GENERATE_BILLPOS, NAME, TECH_EXPORT, DISABLE_NEW, OE__NO, LER_NO, LEISTUNGKAT, CORRECTION,
  RF_FEE_PERIOD, RF_DUE_PERIOD, RF_ALIGNED_PERIOD, RF_PRO_RATA, RF_PAYBACK, LEISTUNGTYP, PRICE_SCHEME, USER_PRICE_SCHEME,
  SEPARATED_BILLPOS, PROPORTIONAL, PREIS_EDITIERBAR, PREIS, WAEHRUNG_ID, VAT_INCLUDED, MENGE_EDITIERBAR,
  MENGE, MASS, MWST_SATZ, BILLING_PRICE_SOURCE, BILLING_QUANTITY_SOURCE, BS_RELEVANT, USERW, DATEW, EXT_PRODUKT__NO,
  MAY_OVERLAP)
  values 
  (40832, 40832, '1996-01-01', '9999-12-31', 'AKT', 0, 1,
  1, 0, 'Zusatzleitung', 1, 0, 2159, 1, 'WIEDERHOLT     ', 0, 
  1, 0, 1, 1, 0, 'OPTIONAL        ', 'FIX', 'FIX', 
  0, 0, 0, 0, 'EUR', 0, 1, 
  1, 'Stck    ', 19, 'service_element', 'service_element', 0, 'MNETTEST  ', '2008-01-20', '456',
  0);
insert into LEISTUNG (LEISTUNG_NO, LEISTUNG__NO, GUELTIG_VON, GUELTIG_BIS, HIST_STATUS, HIST_CNT, HIST_LAST,
  PRIMARY_INSTANCE, GENERATE_BILLPOS, NAME, TECH_EXPORT, DISABLE_NEW, OE__NO, LER_NO, LEISTUNGKAT, CORRECTION,
  RF_FEE_PERIOD, RF_DUE_PERIOD, RF_ALIGNED_PERIOD, RF_PRO_RATA, RF_PAYBACK, LEISTUNGTYP, PRICE_SCHEME, USER_PRICE_SCHEME,
  SEPARATED_BILLPOS, PROPORTIONAL, PREIS_EDITIERBAR, PREIS, WAEHRUNG_ID, VAT_INCLUDED, MENGE_EDITIERBAR,
  MENGE, MASS, MWST_SATZ, BILLING_PRICE_SOURCE, BILLING_QUANTITY_SOURCE, BS_RELEVANT, USERW, DATEW, EXT_PRODUKT__NO,
  MAY_OVERLAP)
  values 
  (40833, 40833, '1996-01-01', '9999-12-31', 'AKT', 0, 1,
  1, 0, 'Zusatzleitung', 1, 0, 2160, 1, 'WIEDERHOLT     ', 0, 
  1, 0, 1, 1, 0, 'OPTIONAL        ', 'FIX', 'FIX', 
  0, 0, 0, 0, 'EUR', 0, 1, 
  1, 'Stck    ', 19, 'service_element', 'service_element', 0, 'MNETTEST  ', '2008-01-20', '456',
  0);
insert into LEISTUNG (LEISTUNG_NO, LEISTUNG__NO, GUELTIG_VON, GUELTIG_BIS, HIST_STATUS, HIST_CNT, HIST_LAST,
  PRIMARY_INSTANCE, GENERATE_BILLPOS, NAME, TECH_EXPORT, DISABLE_NEW, OE__NO, LER_NO, LEISTUNGKAT, CORRECTION,
  RF_FEE_PERIOD, RF_DUE_PERIOD, RF_ALIGNED_PERIOD, RF_PRO_RATA, RF_PAYBACK, LEISTUNGTYP, PRICE_SCHEME, USER_PRICE_SCHEME,
  SEPARATED_BILLPOS, PROPORTIONAL, PREIS_EDITIERBAR, PREIS, WAEHRUNG_ID, VAT_INCLUDED, MENGE_EDITIERBAR,
  MENGE, MASS, MWST_SATZ, BILLING_PRICE_SOURCE, BILLING_QUANTITY_SOURCE, BS_RELEVANT, USERW, DATEW, EXT_PRODUKT__NO,
  MAY_OVERLAP)
  values 
  (40834, 40834, '1996-01-01', '9999-12-31', 'AKT', 0, 1,
  1, 0, 'Zusatzleitung', 1, 0, 2161, 1, 'WIEDERHOLT     ', 0, 
  1, 0, 1, 1, 0, 'OPTIONAL        ', 'FIX', 'FIX', 
  0, 0, 0, 0, 'EUR', 0, 1, 
  1, 'Stck    ', 19, 'service_element', 'service_element', 0, 'MNETTEST  ', '2008-01-20', '456',
  0);
insert into LEISTUNG (LEISTUNG_NO, LEISTUNG__NO, GUELTIG_VON, GUELTIG_BIS, HIST_STATUS, HIST_CNT, HIST_LAST,
  PRIMARY_INSTANCE, GENERATE_BILLPOS, NAME, TECH_EXPORT, DISABLE_NEW, OE__NO, LER_NO, LEISTUNGKAT, CORRECTION,
  RF_FEE_PERIOD, RF_DUE_PERIOD, RF_ALIGNED_PERIOD, RF_PRO_RATA, RF_PAYBACK, LEISTUNGTYP, PRICE_SCHEME, USER_PRICE_SCHEME,
  SEPARATED_BILLPOS, PROPORTIONAL, PREIS_EDITIERBAR, PREIS, WAEHRUNG_ID, VAT_INCLUDED, MENGE_EDITIERBAR,
  MENGE, MASS, MWST_SATZ, BILLING_PRICE_SOURCE, BILLING_QUANTITY_SOURCE, BS_RELEVANT, USERW, DATEW, EXT_PRODUKT__NO,
  MAY_OVERLAP)
  values 
  (40835, 40835, '1996-01-01', '9999-12-31', 'AKT', 0, 1,
  1, 0, 'Zusatzleitung', 1, 0, 2162, 1, 'WIEDERHOLT     ', 0, 
  1, 0, 1, 1, 0, 'OPTIONAL        ', 'FIX', 'FIX', 
  0, 0, 0, 0, 'EUR', 0, 1, 
  1, 'Stck    ', 19, 'service_element', 'service_element', 0, 'MNETTEST  ', '2008-01-20', '456',
  0);

-- TODO (manuell) Sequence S_LEISTUNG_0 hoch setzen  
select * from dba_sequences where sequence_name like 'S_LEISTUNG_0';
drop sequence S_LEISTUNG_0;
create sequence S_LEISTUNG_0 start with ???;
grant select on S_LEISTUNG_0 to public;
commit;


-- Werte fuer Bereitstellungsleistung korrigieren
--update service_value_price set value='BEREITCON12' where value='BEREITCONN12'; 
--update service_value_price set value='BEREITCON24' where value='BEREITCONN24';
--update service_value_price set value='BEREITCON36' where value='BEREITCONN36';
--update service_value_price set value='BEREITCON48' where value='BEREITCONN48';
--update service_value_price set value='BEREITCON60' where value='BEREITCONN60';
--update service_value_price set value='BEREITCONSON' where value='BEREITCONNSON';


-- FIBU_ACCOUNT in Werteliste setzen (manuell)
select min(leistung_no) as min_no, max(leistung_no) as max_no 
  from leistung l where l.oe__no between 2157 and 2162;
  
update service_value_price set FIBU_ACCOUNT=VALUE 
  where FIBU_ACCOUNT is null and leistung_no between ? and ?;
  


-- Gueltigkeit der Leistungen in DirectAccess korrigieren
update leistung set gueltig_von='1996-01-01' where oe__no=2161;
  



-- TODO (manuell) Wertelisten in Produkt DirectAccess korrigieren
--      jede Leistung besitzt hier alle Werte. Muss korrigiert werden, damit Leistung nur noch
--      der Bandbreite entsprechende Werte besitzt.


--select min(leistung_no) as min_no, max(leistung_no) as max_no 
--  from leistung l where l.oe__no between 2113 and 2156;

--delete from service_value_price where leistung_no between 40338 and 40742;
--delete from leistung where leistung_no between 40338 and 40742;

