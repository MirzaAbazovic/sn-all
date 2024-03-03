delete from T_DB_QUERIES where ID=10;

Insert into T_DB_QUERIES
   (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY,
    PARAMS, VERSION)
 Values
   (10, 'Auftrag gekündigt - aktive Position', 'Ermittelt gekündigte Aufträge, die noch Positionen ohne gueltig-bis Datum haben', 'BILLING', 'select c.cust_no, c.reseller_cust_no, ap.order__no, ap.item_no, ap.price, ap.list_price, ap.charge_to as AP_gueltig,
a.valid_from as A_gueltig, l.name, ap.USERW as BEARBEITER
from auftragpos ap
inner join auftrag a on ap.order__no=a.auftrag__no
inner join customer c on a.cust_no=c.cust_no
inner join leistung l on ap.service_elem__no=l.leistung__no
where ap.charge_to is null
and (ap.free_text is null or ap.free_text='''')
and a.hist_last=''1'' and a.hist_status=''ALT'' and a.valid_to<to_date(''31.12.9999'', ''dd.mm.yyyy'')
and l.hist_last=''1''',
    null, 0);
