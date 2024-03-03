--
-- Einen UNKNOWN referenz-typ fuer cross connections hinzufuegen
--


-- Referenz-Werte fuer die CrossConnection Types anlegen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (20005, 'XCONN_TYPES', 'UNKNOWN_XCONN', '0', 100, 'Unknown CrossConnection type');
