-- Referenzen fuer GeoID Kl�rung Stauts anlegen
insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 Values
   (22300, 'GEOID_CLARIFICATION_STATUS', 'OPEN', NULL, NULL,
    NULL, '1', 10, NULL, 0);
insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 Values
   (22301, 'GEOID_CLARIFICATION_STATUS', 'SOLVED', NULL, NULL,
    NULL, '1', 20, NULL, 0);
insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 Values
   (22302, 'GEOID_CLARIFICATION_STATUS', 'CLOSED', NULL, NULL,
    NULL, '1', 30, NULL, 0);

-- Referenzen fuer GeoID Kl�rung Typ anlegen
insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 Values
   (22310, 'GEOID_CLARIFICATION_TYPE', 'ONKZ_ASB', NULL, NULL,
    NULL, '1', 10, NULL, 0);
