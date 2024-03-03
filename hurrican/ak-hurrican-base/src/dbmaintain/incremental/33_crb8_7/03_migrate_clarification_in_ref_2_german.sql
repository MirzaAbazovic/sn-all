-- GEOID_CLARIFICATION_STATUS
update T_REFERENCE set STR_VALUE='offen' where ID=22300;
update T_REFERENCE set STR_VALUE='gelöst' where ID=22301;
update T_REFERENCE set STR_VALUE='geschlossen' where ID=22302;
update T_REFERENCE set STR_VALUE='in Bearbeitung' where ID=22303;

-- GEOID_CLARIFICATION_TYPE
update T_REFERENCE set STR_VALUE='Abweichung ONKZ/ASB' where ID=22310;
update T_REFERENCE set STR_VALUE='Erstellung Geo ID' where ID=22311;
update T_REFERENCE set STR_VALUE='Aktualisierung Geo ID' where ID=22312;
update T_REFERENCE set STR_VALUE='Zuordnung KVZ', ORDER_NO=40 where ID=22313;
update T_REFERENCE set STR_VALUE='Abweichung KVZ', ORDER_NO=50 where ID=22314;