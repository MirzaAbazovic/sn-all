insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'Sandreuthstr. 31', 'Sandreuthstr.', '31', '90441', 'Nürnberg', 'DE');
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'Sandreuthstr. 31', (select ID from T_ADDRESS where VORNAME='Sandreuthstr. 31'));
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (20, (select ID from T_HOUSING_BUILDING where BUILDING='Sandreuthstr. 31'), 'UG');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 20, 'Raum 7');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 20, 'Raum 8');


