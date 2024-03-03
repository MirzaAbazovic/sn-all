-- Usecase Anbieterwechsel hinzufuegen

-- Neuer Eintrag in T_Reference - Service Chain fürs erste TAL-NEU
INSERT INTO T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 VALUES
   (8004, 'TAL_BESTELLUNG_TYPE', 'Anbieterwechsel', NULL, NULL,
    NULL, '1', 40, 'Typ, fuer eine TAL-Bestellung - Vorgang: Anbieterwechsel; INT_VALUE = ID der ServiceChain', 0);

Insert into T_CB_USECASE (ID, REFERENCE_ID, WITA_GESCHAEFTSFALL, ACTIVE, VERSION)
 Values (105, 8004, 'Anbieterwechsel', '0', 0);
