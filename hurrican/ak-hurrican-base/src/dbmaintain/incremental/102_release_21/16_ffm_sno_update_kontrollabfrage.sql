-- HUR-24680 Die Kontrollabfrage ermoeglicht ueber die GUI Klaerfaelle aus den FFM Seriennummern Updates zu ermitteln
-- Genauer die letzten 250 Klaerfaelle

INSERT INTO T_DB_QUERIES
(ID, NAME, DESCRIPTION, SERVICE,
 SQL_QUERY,
 PARAMS, VERSION, NOT_FOR_TEST)
VALUES
  (S_T_DB_QUERIES_0.nextval, 'FFM Seriennummern Update', 'Ermittelt die letzten 250 Klärfälle', 'CC',
   'SELECT ID, ERROR_NAME, ERROR_DESCRIPTION, CREATED_AT, SERVICE, STACKTRACE FROM T_ERROR_LOG WHERE ROWNUM <= 250 ORDER BY CREATED_AT DESC NULLS LAST',
   NULL, 0, '0');
