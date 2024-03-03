--
-- SQL-Script, um die notwendigen Referenz-Werte für die
-- Wita-Gesschäftsfälle einzutragen
--

-- Anlegen von Referenz-Werte fuer die TAL_Bestellung Types
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (8006, 'TAL_BESTELLUNG_TYP', 'Leistungsänderung', 46, '1', 160, 'Typ fuer eine TAL-Bestellung - Vorgang: Leistungsänderung; INT_VALUE = ID der ServiceChain');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (8007, 'TAL_BESTELLUNG_TYP', 'Leistungsmerkmaländerung', 47, '1', 170, 'Typ fuer eine TAL-Bestellung - Vorgang: Leistungsänderung; INT_VALUE = ID der ServiceChain');
UPDATE T_REFERENCE SET INT_VALUE = 44, ORDER_NO = 140, GUI_VISIBLE = '1' WHERE ID = 8004;
UPDATE T_REFERENCE SET INT_VALUE = 45, ORDER_NO = 150, TYPE = 'TAL_BESTELLUNG_TYP', GUI_VISIBLE = '1' WHERE ID = 8005;
update T_REFERENCE set ORDER_NO = 110 where ID = 8001;
update T_REFERENCE set ORDER_NO = 120 where ID = 8002;
update T_REFERENCE set ORDER_NO = 130 where ID = 8003;