ALTER TABLE T_WBCI_REQUEST add VA_KUNDENWUNSCHTERMIN DATE;

UPDATE T_WBCI_REQUEST r
  SET VA_KUNDENWUNSCHTERMIN = (
   		select distinct to_date(REGEXP_SUBSTR(io.REQUEST_XML, '[^kundenwunschtermin>]\d+\-\d+\-\d+', 1, 1, 'i'), 'YYYY-MM-DD') as VA_KUNDENWUNSCHTERMIN from T_WBCI_REQUEST req
		  left join T_WBCI_GESCHAEFTSFALL gf on gf.ID = req.GESCHAEFTSFALL_ID
		  left join T_IO_ARCHIVE io on gf.VORABSTIMMUNGSID = io.WITA_EXT_ORDER_NO
		  where req.id = r.id and io.IO_SOURCE='WBCI' and io.REQUEST_TYP = 'VA' and req.TYP = 'VA'
		      and TIMESTAMP_SENT = (select max(TIMESTAMP_SENT) from T_IO_ARCHIVE where WITA_EXT_ORDER_NO = io.WITA_EXT_ORDER_NO and request_typ = 'VA')
  )
WHERE EXISTS (
	  select 1
	  from T_WBCI_REQUEST req
	  left join T_WBCI_GESCHAEFTSFALL gf on gf.ID = req.GESCHAEFTSFALL_ID
	  where req.id = r.id and req.TYP = 'VA'
);
