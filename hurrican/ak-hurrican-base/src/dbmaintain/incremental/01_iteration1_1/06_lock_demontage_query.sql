--
-- SQL-Script, um eine neue Kontrollabfrage fuer AM zu erstellen.
-- Abfrage ermittelt alle aktiven Demontagen.
--

insert into T_DB_QUERIES (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY)
  values (20, 'Demontagen', 'Ermittlung der durchzufuehrenden Demontagen.',
  'CC',
  'SELECT l.CUSTOMER_NO as KUNDE__NO, l.DEB_ID as DEBITOR_ID, l.TAIFUN_ORDER__NO as TAIFUN_ORDER__NO,
  l.AUFTRAG_ID as AUFTRAG_ID from T_LOCK l where l.LOCK_MODE_REF_ID=1504 and l.LOCK_STATE_REF_ID=1510');
commit;
