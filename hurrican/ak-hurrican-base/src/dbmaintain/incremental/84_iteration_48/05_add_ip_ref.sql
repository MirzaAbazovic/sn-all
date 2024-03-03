-- Referenz-Wert fuer Main-Kinzig anlegen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
    values (22403, 'IP_BACKBONE_LOCATION_TYPE', 'MKK', 4, '1', 40, 'IP Backbone Standort Main-Kinzig Kreis');

-- Die Niederlassung Main-Kinzig Kreis bekommt die neu IP_LOCATION zugeordnet
update t_NIEDERLASSUNG set IP_LOCATION = 22403 where ID = 6;