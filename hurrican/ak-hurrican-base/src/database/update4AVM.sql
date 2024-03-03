--
-- SQL-Script, um die AVM-Endgeraete in Hurrican anzulegen
-- und mit den entsprechenden Taifun-Leistungen zu verbinden.
--

alter session set nls_date_format='yyyy-mm-dd';

-- Endgeraete aktualisieren
update T_EG set GARANTIEZEIT='24 Monate' where ID in (39,40,41);
update T_EG set EXT_LEISTUNG__NO=50017 where ID=39;
update T_EG set LS_TEXT='AVM Fritz!Box WLAN DSL-Router 3170' where id in (39,40);
commit;

-- Pakete definieren
-- Paket mit AVM FB3170 und NTSplit fuer DSL+ISDN
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
  VERFUEGBAR_VON, VERFUEGBAR_BIS, TYPE)
  values (42, 42, 'Set AVM FritzBox3170 ISDN', 'Paket mit AVM FB3170 und NTSplit', 'Set AVM FritzBox3170 ISDN', 50017,
  '2008-09-01', '2200-01-01', 5);
insert into T_EG_2_PAKET (ID, EG_INTERNE__ID, PAKET_ID) values (3, 39, 42);
insert into T_EG_2_PAKET (ID, EG_INTERNE__ID, PAKET_ID) values (4, 32, 42);
-- Paket mit AVM FB3170 und Splitter fuer DSL+analog
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
  VERFUEGBAR_VON, VERFUEGBAR_BIS, TYPE)
  values (43, 43, 'Set AVM FritzBox3170 a/b', 'Paket mit AVM FB3170 und Splitter', 'Set AVM FritzBox3170 a/b', 50017,
  '2008-09-01', '2200-01-01', 5);
insert into T_EG_2_PAKET (ID, EG_INTERNE__ID, PAKET_ID) values (5, 39, 43);
insert into T_EG_2_PAKET (ID, EG_INTERNE__ID, PAKET_ID) values (6, 31, 43);
-- Paket fuer DSL+ISDN; enthaelt nur den NTSplit
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
  VERFUEGBAR_VON, VERFUEGBAR_BIS, TYPE)
  values (44, 44, 'Set DSL ISDN ohne Modem', 'Paket mit NTSplit', 'Set DSL ISDN', 50018,
  '2008-09-01', '2200-01-01', 5);
insert into T_EG_2_PAKET (ID, EG_INTERNE__ID, PAKET_ID) values (7, 32, 44);
-- Paket fuer DSL+a/b; enthaelt nur den Splitter
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
  VERFUEGBAR_VON, VERFUEGBAR_BIS, TYPE)
  values (45, 45, 'Set DSL a/b ohne Modem', 'Paket mit Splitter', 'Set DSL a/b', 50018,
  '2008-09-01', '2200-01-01', 5);
insert into T_EG_2_PAKET (ID, EG_INTERNE__ID, PAKET_ID) values (8, 31, 45);
-- Paket mit AVM FB3170 DSLpur
insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
  VERFUEGBAR_VON, VERFUEGBAR_BIS, TYPE)
  values (46, 46, 'Set AVM FritzBox3170 Pur', 'Paket mit AVM FB3170', 'Set AVM FritzBox3170 Pur', 50017,
  '2008-09-01', '2200-01-01', 5);
insert into T_EG_2_PAKET (ID, EG_INTERNE__ID, PAKET_ID) values (9, 39, 46);
commit;


-- Zuordnung Endgeraete zu Produkten
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (123, 420, 39, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (124, 421, 39, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (125, 430, 39, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (126, 431, 39, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (127, 440, 39, '0');
commit;

-- Zuordnung der Pakete zu Produkten
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (128, 420, 42, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (129, 420, 44, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (130, 421, 43, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (131, 421, 45, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (132, 430, 42, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (133, 430, 44, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (134, 431, 43, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (135, 431, 45, '0');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT) values (136, 440, 46, '0');
commit;

-- Sequence aktualisieren
drop sequence S_T_PROD_2_EG_0;
create sequence S_T_PROD_2_EG_0 start with 137;
grant select on S_T_PROD_2_EG_0 to public;
commit;


-- ServiceCommand fuer AVM FritzBox definieren
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (500, 'eg.check.billing.pos', 
  'de.augustakom.hurrican.service.cc.impl.command.eg.EGCheckExistingBillPosCommand', 
  'EG_CHECK', 'ServiceCommand prueft, ob im Billing-Auftrag eine bestimmte Endgeraete-Leistung gebucht ist.');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
  values (720, 500, 39, 'de.augustakom.hurrican.model.cc.EG', null);

drop sequence S_T_SERVICE_COMMAND_MAPPI_0;
create sequence S_T_SERVICE_COMMAND_MAPPI_0 start with 720;
grant select on S_T_SERVICE_COMMAND_MAPPI_0 to public;
commit;

