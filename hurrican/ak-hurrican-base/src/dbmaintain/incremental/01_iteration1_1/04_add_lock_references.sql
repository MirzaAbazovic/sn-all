--
-- Script legt weitere Reference-Eintraege fuer Sperren an
--

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1521, 'LOCK_REASON', 'Zahlungsverzug', '1', 20, 'Sperre wegen Zahlungsverzug');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1522, 'LOCK_REASON', 'Ratenvereinbarung', '1', 30, 'Sperre wegen Ratenvereinbarung');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1523, 'LOCK_REASON', 'Highspender', '1', 40, 'Sperre wegen Highspender');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1524, 'LOCK_REASON', 'Sonstiges', '1', 50, 'Sperre wegen Sonstiges');
commit;
