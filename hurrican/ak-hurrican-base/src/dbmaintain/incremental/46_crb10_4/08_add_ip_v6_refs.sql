-- Referenz-Werte fuer 'IPPurposeTypes' anlegen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
    values (22370, 'IP_PURPOSE_TYPE', 'Kundennetz', 1, '1', 10, 'IP Anforderung: Kundennetz');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
    values (22371, 'IP_PURPOSE_TYPE', 'Transfernetz', 2, '1', 20, 'IP Anforderung: Transfernetz');

-- Referenz-Werte fuer 'IPBackboneLocationTypes' anlegen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
    values (22400, 'IP_BACKBONE_LOCATION_TYPE', 'MUC', 1, '1', 10, 'IP Backbone Standort München');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
    values (22401, 'IP_BACKBONE_LOCATION_TYPE', 'AGB', 2, '1', 20, 'IP Backbone Standort Augsburg');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
    values (22402, 'IP_BACKBONE_LOCATION_TYPE', 'NUE', 3, '1', 30, 'IP Backbone Standort Nürnberg');
