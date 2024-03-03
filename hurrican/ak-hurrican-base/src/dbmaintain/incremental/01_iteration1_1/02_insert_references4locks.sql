--
-- SQL-Script legt Referenz-Daten fuer die Locks an.
--

-- Referenz-Daten fuer Sperr-Modus eintragen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1504, 'LOCK_MODE', 'Demontage', '0', 50, 'Demontage');
update T_REFERENCE set GUI_VISIBLE='0' where ID in (1502,1503);
commit;

-- Referenz-Daten fuer Sperr-Status eintragen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1510, 'LOCK_STATE', 'active', '1', null, 'Sperrvorgang ist aktiv');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1511, 'LOCK_STATE', 'finished', '1', null, 'Sperrvorgang ist erledigt');
commit;

-- Referenz-Daten fuer Sperr-Gruende eintragen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1520, 'LOCK_REASON', 'Insolvenz', '1', 10, 'Sperre wegen Insolvenz');
-- TODO Liste erweitern mit Vorgaben von Fr. Weber
commit;
  


