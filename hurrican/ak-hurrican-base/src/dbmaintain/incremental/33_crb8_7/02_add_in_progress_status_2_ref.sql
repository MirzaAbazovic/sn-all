-- Status 'In Progress' f�r Geo ID Kl�rung anlegen
insert into T_REFERENCE
   (ID, TYPE, STR_VALUE, INT_VALUE, FLOAT_VALUE,
    UNIT_ID, GUI_VISIBLE, ORDER_NO, DESCRIPTION, VERSION)
 Values
   (22303, 'GEOID_CLARIFICATION_STATUS', 'IN_PROGRESS', NULL, NULL,
    NULL, '1', 20, NULL, 0);

update T_REFERENCE set DESCRIPTION='Kl�rung ist offen' where ID=22300;
update T_REFERENCE set DESCRIPTION='Kl�rung ist gel�st', ORDER_NO=30  where ID=22301;
update T_REFERENCE set DESCRIPTION='Kl�rung geschlossen, da kein Aufwand' where ID=22302;
update T_REFERENCE set DESCRIPTION='Kl�rung in Bearbeitung'  where ID=22303;
