
delete from T_DB_QUERIES where ID=6;

Insert into T_DB_QUERIES
   (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY,
    PARAMS, VERSION)
 Values
   (6, 'Invalid Bill-Channel', 'Ermittlung von RInfos mit falschem Rechnungskanal (f. Maxi und Premium)', 'BILLING', 'select distinct b.BILL_SPEC_NO, b.EXT_DEBITOR_ID, c.CUST_NO, b.USERW, b.INV_MAXI
from CUSTOMER c, AUFTRAG a, BILL_SPEC b
where a.OE__NO in  (2000,2001,2002,2005,2006,2007,2008,2011,2012,2013,2014,2015,2111,3007,30,31,32,33,34,35,100,101,102,103,104,105,106,111,112,113,114,115,40,41,42,43,44,45,46,47,48,49)
and a.BILL_SPEC_NO=b.BILL_SPEC_NO
and a.hist_last=''1'' and a.hist_status<>''UNG''
and b.INV_MAXI=''0''
and b.CUST_NO=c.CUST_NO
and c.RESELLER_CUST_NO in (100000009, 400000001)',
    NULL, 0);
