delete from T_DB_QUERIES where ID in (134);

insert into T_DB_QUERIES (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY, PARAMS)
  values (134, 'Aufträge mit Papierrechung',
  'Zeigt alle Aufträge an, die als Rechnungsspezifikation das Flag "Papierrechnung" gesetzt haben und keine Papier-Rechnung als Auftragsposition besitzen',
  'BILLING',
  'SELECT AUFTRAG__NO AS Auftrag, CUST_NO as Kundennr, CUS.NAME as Nachname, CUS.FIRSTNAME Vorname, AR.DESCRIPTION Niederlassung, OE.NAME as Produkt FROM AUFTRAG A
        JOIN BILL_SPEC BS ON BS.BILL_SPEC_NO = A.BILL_SPEC_NO and BS.INV_PAPER = 1
        JOIN CUSTOMER CUS ON A.CUST_NO = CUS.CUST_NO
        JOIN AREA AR ON CUS.AREA_NO = AR.AREA_NO
        JOIN OE ON A.OE__NO = OE.OE__NO and OE.HIST_LAST = 1
    where A.VALID_FROM<SYSDATE AND A.VALID_TO>SYSDATE and A.HIST_STATUS =''AKT'' and A.VERTRAGSDATUM between trunc(?) and trunc(?) AND A.AUFTRAG__NO not in
        (select ORDER__NO from AUFTRAGPOS AP join LEISTUNG LE ON AP.SERVICE_ELEM__NO = LE .LEISTUNG__NO AND LE.NAME like ''%Papier%'')',
    'Vertragsdatum_von=date, Vertragsdatum_bis=date');