-- Usecase Portwechsel hinzufuegen (wird spaeter wieder geloescht, wenn die Entscheidung 
-- LAE/LMAE/SER-POW automatisiert wird

-- Neuer Eintrag in T_Reference - Service Chain fürs erste TAL-NEU
INSERT INTO T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 VALUES
   (8005, 'TAL_BESTELLUNG_TYPE', 'Portwechsel', 40, NULL,
    NULL, '1', 40, 'Typ, fuer eine TAL-Bestellung - Vorgang: Portwechsel; INT_VALUE = ID der ServiceChain', 0);

Insert into T_CB_USECASE (ID, REFERENCE_ID, WITA_GESCHAEFTSFALL, ACTIVE, VERSION)
 Values (106, 8005, 'Anbieterwechsel', '0', 0);
