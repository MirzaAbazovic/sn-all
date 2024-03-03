-- Neuen Service Order Typ 'queryHardware' erstellen
Insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, GUI_VISIBLE, DESCRIPTION, VERSION)
 Values
   (14008, 'CPS_SERVICE_ORDER_TYPE', 'queryHardware', '0', 'Wert definiert einen ServiceOrder-Typ, um Hardware Stati/Werte abzufragen', 0);
