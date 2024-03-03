--
-- SQL-Script, um eine neue Kontrollabfrage fuer AM zu erstellen.
-- Abfrage ermittelt gekündigte Carrierbestellungen, die vom betroffenen Carrier noch nicht bestätigt wurden.
--

insert into T_DB_QUERIES (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY)
  values (S_T_DB_QUERIES_0.nextval, 'Kündigung von Carrierbestellungen', 'Ermittelung gekündigter Carrierbestellungen, die vom betroffenen Carrier noch nicht bestätigt wurden.',
  'BILLING',
  'SELECT AUF.ID AUFTRAG, AD.PRODAK_ORDER__NO TAIFUN_ORDER, AST.STATUS_TEXT STATUS, NI.TEXT NIEDERLASSUNG, AD.KUENDIGUNG, CB.LBZ LEITUNGSBEZ, CB.VTRNR VERTRAGNR, CB.KUENDIGUNG_AN_CARRIER, CB.KUENDBESTAETIGUNG_CARRIER FROM T_CARRIERBESTELLUNG CB
        INNER JOIN T_AUFTRAG AUF ON AUF.ID = CB.AUFTRAG_ID_4_TAL_NA
        INNER JOIN T_AUFTRAG_DATEN AD ON AD.ID = AUF.ID
        INNER JOIN T_AUFTRAG_STATUS AST ON AD.STATUS_ID = AST.ID
        INNER JOIN T_AUFTRAG_TECHNIK AT ON AUF.ID = AT.ID
        LEFT  JOIN T_NIEDERLASSUNG NI ON AT.NIEDERLASSUNG_ID = NI.ID
    WHERE  CB.LBZ IS NOT NULL
        AND CB.VTRNR IS NOT NULL
        AND CB.KUENDIGUNG_AN_CARRIER IS NOT NULL
        AND CB.KUENDBESTAETIGUNG_CARRIER IS NULL
        AND AD.KUENDIGUNG IS NOT NULL;');