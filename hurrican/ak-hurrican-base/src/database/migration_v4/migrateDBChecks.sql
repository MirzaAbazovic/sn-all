--
-- SQL-Script, um die DB-Queries an V4.3 anzupassen.
--

-- ID 6
update T_DB_QUERIES set 
  SQL_QUERY='select distinct b.BILL_SPEC_NO, b.EXT_DEBITOR_ID, c.CUST_NO, b.USERW, b.INV_MAXI 
from CUSTOMER c, AUFTRAG a, BILL_SPEC b 
where a.OE__NO in  (2000,2001,2002,2005,2006,2007,2008,2011,2012,2013,2014,2111,30,31,32,33,34,35,100,101,102,103,104,105,106,111,112,113,114,115,40,41,42,43,44,45,46,47,48,49) 
and a.BILL_SPEC_NO=b.BILL_SPEC_NO 
and a.hist_last=''1'' and a.hist_status<>''UNG''
and b.INV_MAXI=''0'' 
and b.CUST_NO=c.CUST_NO 
and c.RESELLER_CUST_NO in (100000009, 400000001)' 
  where ID=6;

-- ID 7
update T_DB_QUERIES set 
  SQL_QUERY='SELECT c.CUST_NO, c.RESELLER_CUST_NO, b.EXT_DEBITOR_ID
FROM CUSTOMER c
INNER JOIN BILL_SPEC b ON c.CUST_NO = b.CUST_NO
WHERE ( (((c.RESELLER_CUST_NO) In (100000009,400000001)) AND ((b.EXT_DEBITOR_ID) Like ''M%''))
or (((c.RESELLER_CUST_NO) In (100000081)) AND ((b.EXT_DEBITOR_ID) Like ''A%'')) )
and c.CUST_NO not in (100000081)' 
  where ID=7;

-- ID 8
update T_DB_QUERIES set 
  SQL_QUERY='select distinct ap.ORDER__NO, ap.CHARGE_FROM, ap.CHARGE_TO, ap.USERW as BEARBEITER, ar.DESCRIPTION as NIEDERLASSUNG, l.NAME
from AUFTRAGPOS ap
inner join LEISTUNG l on ap.SERVICE_ELEM__NO=l.LEISTUNG__NO
inner join AUFTRAG a on ap.ORDER__NO=a.AUFTRAG__NO
inner join CUSTOMER c on a.CUST_NO=c.CUST_NO
inner join AREA ar on c.AREA_NO=ar.AREA_NO
where l.NAME like ''G/%'' and l.NAME not like ''G/Maxi Mitarbeiterrabatt%'' and l.NAME not like ''G/SDSL%'' and l.NAME not like ''G/4%''  and l.NAME not like ''G/6%'' and l.NAME not like ''G/8%''
and l.NAME not like ''G/3%'' and l.NAME not like ''G/50%'' and l.NAME not like ''G/%CompanyPLUS%'' and l.NAME not like ''G/Kooperation%'' and l.HIST_LAST=''1'' and ap.CHARGE_TO is null' 
  where ID=8;

-- ID 9
update T_DB_QUERIES set 
  SQL_QUERY='select c.CUST_NO, c.reseller_cust_no, ap.order__no, ap.item_no, ap.price, ap.list_price, ap.charge_to as AP_gueltig, a.valid_to as A_gueltig, l.name, ap.USERW as BEARBEITER
from auftragpos ap
inner join auftrag a on ap.order__no=a.auftrag__no
inner join customer c on a.cust_no=c.cust_no
inner join leistung l on ap.service_elem__no=l.leistung__no
where ap.charge_to>a.valid_to
and (ap.free_text is null or ap.free_text='''')
and ap.charge_to>=?
and a.hist_last=''1'' and a.hist_status<>''UNG''
and l.hist_last=''1''' 
  where ID=9;
  
-- ID 10
update T_DB_QUERIES set 
  SQL_QUERY='select c.cust_no, c.reseller_cust_no, ap.order__no, ap.item_no, ap.price, ap.list_price, ap.charge_to as AP_gueltig, 
a.valid_from as A_gueltig, l.name, ap.USERW as BEARBEITER
from auftragpos ap
inner join auftrag a on ap.order__no=a.auftrag__no
inner join customer c on a.cust_no=c.cust_no
inner join leistung l on ap.service_elem__no=l.leistung__no
where ap.charge_to is null
and (ap.free_text is null or ap.free_text='''')
and a.hist_last=''1'' and a.hist_status=''ALT'' and a.valid_to<?
and l.hist_last=''1''' 
  where ID=10;
  

-- ID 12
update T_DB_QUERIES set 
  SQL_QUERY='select ap.order__no, a.cust_no, l.name, ap.charge_from, ap.charge_to, ap.userw from AUFTRAGPOS ap INNER JOIN AUFTRAG a on ap.order__no=a.auftrag__no INNER JOIN LEISTUNG l on ap.service_elem__no=l.leistung__no where a.hist_last=''1'' and a.hist_status<>''UNG'' and l.hist_status=''AKT'' and ap.charge_from>ap.charge_to and ap.charge_from>=?' 
  where ID=12;



