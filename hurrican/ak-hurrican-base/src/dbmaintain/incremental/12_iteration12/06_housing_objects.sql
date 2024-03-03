-- SQL Script, um die Housing Referenzdaten anzulegen

insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'RZ Domagkstr 17', 'Domagkstr', '17', '80807', 'München', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'Level 3 - Wamslerstr 8', 'Wamslerstr', '8', '81829', 'München', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'Betriebsraum LA 52', 'Landshuter Allee', '52', '80637', 'München', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'Briennerstr 18', 'Briennerstr', '18', '80333', 'München', 'DE');


insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'RZ (Domagkstr 17)', (select ID from T_ADDRESS where VORNAME='RZ Domagkstr 17'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'Level 3', (select ID from T_ADDRESS where VORNAME='Level 3 - Wamslerstr 8'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'Betriebsraum (LA52)', (select ID from T_ADDRESS where VORNAME='Betriebsraum LA 52'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'BLB', (select ID from T_ADDRESS where VORNAME='Briennerstr 18'));


insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (1, (select ID from T_HOUSING_BUILDING where BUILDING='RZ (Domagkstr 17)'), 'EG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (2, (select ID from T_HOUSING_BUILDING where BUILDING='Level 3'), 'EG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (3, (select ID from T_HOUSING_BUILDING where BUILDING='Betriebsraum (LA52)'), 'EG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (4, (select ID from T_HOUSING_BUILDING where BUILDING='BLB'), '1 UG');
drop sequence S_T_HOUSING_FLOOR_0;
create SEQUENCE S_T_HOUSING_FLOOR_0 start with 100;
grant select on S_T_HOUSING_FLOOR_0 to public;

insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 1, 'DSR 1');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 1, 'DSR 2');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 1, 'DSR 3');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 1, 'DSR 5');

insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 2, 'Level 3 Cache');

insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 3, 'Housing Room 1');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 3, 'Housing Room 2');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 3, 'Housing Room 3');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 3, 'Housing Room 4');

insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 4, 'A91C');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 4, 'A91D');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 4, 'A91E');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 4, 'A91I');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (S_T_HOUSING_ROOM_0.nextVal, 4, 'A92A');

