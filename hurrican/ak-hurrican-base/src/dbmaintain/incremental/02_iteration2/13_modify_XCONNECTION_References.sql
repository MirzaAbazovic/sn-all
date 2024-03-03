--
-- SQL-Script, um die Referenz-Werte fuer CrossConnection-Typen zu aendern
-- (mit Christoph Luther abgesprochen)
--


-- Referenz-Werte fuer die CrossConnection Types anlegen
delete from T_REFERENCE where ID in (20000,20001,20002);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (20000, 'XCONN_TYPES', 'HSI_XCONN', '1', 10, 'HighSpeedInternet CrossConnection');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (20001, 'XCONN_TYPES', 'VoIP_XCONN', '1', 20, 'VoIP CrossConnection');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (20002, 'XCONN_TYPES', 'IAD_MGM_RB', '1', 30, 'IAD Management Residential Bridge');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (20003, 'XCONN_TYPES', 'CPE_MGM_RB', '1', 30, 'CPE Management Residential Bridge');
commit;
