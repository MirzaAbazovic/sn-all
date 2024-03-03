--
-- SQL-Script legt einen weiteren SO-Type fuer den CPS an.
--

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
    values (14006, 'CPS_SERVICE_ORDER_TYPE', 'lockSubscriber', '0', null,
    'Wert definiert den ServiceOrder-Typ, ueber den eine Sperre durchgefuehrt wird');
commit;
