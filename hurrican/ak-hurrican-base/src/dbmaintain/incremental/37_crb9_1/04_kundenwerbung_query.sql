--
-- SQL-Script, um eine neue Kontrollabfrage fuer AM zu erstellen.
-- Abfrage ermittelt falsch eingetragene Kundenwerbung Auftragspositionen.
--

insert into T_DB_QUERIES (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY)
  values (S_T_DB_QUERIES_0.nextval, 'Kundenwerbung-Gutschriften', 'Ermittlung falsch eingetragener Kundenwerbung-Gutschriften.',
  'BILLING',
  'SELECT P.ORDER__NO,
         a.CUST_NO,
         ar.description AS NIEDERLASSUNG,
         l.NAME AS LEISTUNG,
         p.USERW AS BEARBEITER,
         p.FREE_TEXT AS FREITEXT,
         oe.NAME AS PRODUKT
    FROM    LEISTUNG l
         JOIN
            AUFTRAGPOS p
         ON l.LEISTUNG__NO = p.SERVICE_ELEM__NO
            AND (l.NAME LIKE ''%Gutschrift%Kundenwerbung%''
                 OR l.EXT_MISC__NO = 1033)
            AND p.FREE_TEXT NOT LIKE ''Gutschrift Kundenwerbung:%''
            AND p.CHARGED_UNTIL IS NULL
         JOIN OE oe on l.OE__NO = oe.OE__NO
         join AUFTRAG a on A.AUFTRAG__NO = p.ORDER__NO AND A.HIST_LAST = 1
         join CUSTOMER c on A.CUST_NO = c.CUST_NO
         left join AREA ar on C.AREA_NO = AR.AREA_NO
ORDER BY ORDER__NO DESC');