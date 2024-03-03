

insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'Buswerkstatt Gebaeude 3', 'Lechhauser Str.', '22', '86153', 'Augsburg', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'LEW TelNet', 'Hirtenmahdweg', '8', '86154', 'Augsburg', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'Zentralwerkstatt Gebaeude 10', 'Johannes-Haag-Str.', '7a', '86153', 'Augsburg', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'POP BT(Germany) GmbH & CO OHG', 'Illerstr.', '10', '87435', 'Kempten', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'KFZ-Werkstatt Gebaeude 5/6', 'Johannes-Haag-Str.', '7a', '86153', 'Augsburg', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'Gebaeude AU, EQH 3, AU 4', 'Sulzberg', 'AU 4', '87477', 'Sulzberg', 'DE');
insert into T_ADDRESS (ID, ADDRESS_TYPE, FORMAT_NAME, NAME, VORNAME, STRASSE, HAUSNUMMER, PLZ, ORT, LAND_ID)
  values (S_T_ADDRESS_0.nextVal, 202, 'BUSINESS', 'M-net Housing', 'MCI Worldcom', 'Söflinger Str.', '100', '89077', 'Ulm', 'DE');


insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'Buswerkstatt Gebaeude 3', (select ID from T_ADDRESS where VORNAME='Buswerkstatt Gebaeude 3'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'LEW TelNet', (select ID from T_ADDRESS where VORNAME='LEW TelNet'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'Zentralwerkstatt Gebaeude 10', (select ID from T_ADDRESS where VORNAME='Zentralwerkstatt Gebaeude 10'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'POP BT(Germany) GmbH & CO OHG', (select ID from T_ADDRESS where VORNAME='POP BT(Germany) GmbH & CO OHG'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'KFZ-Werkstatt Gebaeude 5/6', (select ID from T_ADDRESS where VORNAME='KFZ-Werkstatt Gebaeude 5/6'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'Gebaeude AU, EQH 3, AU 4', (select ID from T_ADDRESS where VORNAME='Gebaeude AU, EQH 3, AU 4'));
insert into T_HOUSING_BUILDING (ID, BUILDING, ADDRESS_ID)
  values (S_T_HOUSING_BUILDING_0.nextVal, 'MCI Worldcom', (select ID from T_ADDRESS where VORNAME='MCI Worldcom'));


insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (5, (select ID from T_HOUSING_BUILDING where BUILDING='Buswerkstatt Gebaeude 3'), '2. OG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (6, (select ID from T_HOUSING_BUILDING where BUILDING='LEW TelNet'), '1. OG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (7, (select ID from T_HOUSING_BUILDING where BUILDING='Zentralwerkstatt Gebaeude 10'), 'UG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (8, (select ID from T_HOUSING_BUILDING where BUILDING='POP BT(Germany) GmbH & CO OHG'), 'OG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (9, (select ID from T_HOUSING_BUILDING where BUILDING='KFZ-Werkstatt Gebaeude 5/6'), 'UG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (10, (select ID from T_HOUSING_BUILDING where BUILDING='Gebaeude AU, EQH 3, AU 4'), 'OG');
insert into T_HOUSING_FLOOR (ID, BUILDING_ID, FLOOR)
  values (11, (select ID from T_HOUSING_BUILDING where BUILDING='MCI Worldcom'), '1. UG');


insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (20, 5, '201');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (21, 7, '026');
insert into T_HOUSING_ROOM (ID, FLOOR_ID, ROOM)
  values (22, 11, '06/97');
drop sequence S_T_HOUSING_ROOM_0;
create SEQUENCE S_T_HOUSING_ROOM_0 start with 100;
grant select on S_T_HOUSING_ROOM_0 to public;

alter table T_HOUSING_PARCEL drop constraint FK_HOPARCEL_2_ROOM;
ALTER TABLE T_HOUSING_PARCEL ADD CONSTRAINT FK_HOPARCEL_2_ROOM
  FOREIGN KEY (ROOM_ID) REFERENCES T_HOUSING_ROOM (ID);

insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 20, 'EQH 4/I', 27);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 20, 'EQH 1', NULL);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 20, 'EQH 2', 21);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 20, 'EQH 3', 29);

insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/23', 28);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/16/R1/2', 2);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/11', 10);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/22', 4);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/21', 4);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/20', 6);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/19', 4);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/18', 7);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/17', 25);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/01', NULL);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/02', 3);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/03', 3);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/04', 3);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/05', 3);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/06', 3);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/07', 3);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/08', 3);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/09', 3);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/10', 9);

insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/16/R1/1', NULL);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/12', 10);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/13', 10);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/14', 10);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/15', 10);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/16/R2', NULL);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/17', 9);
insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 21, 'EQH 5/16/R3', NULL);

insert into T_HOUSING_PARCEL (ID, ROOM_ID, PARCEL, QM)
  values (S_T_HOUSING_PARCEL_0.nextVal, 22, 'EQH Ulm I', 26);





