--
-- DB-Aenderungen fuer Maxi Deluxe
--

-- Tabelle T_BA_ZEITFENSTER anlegen. 
-- Tabelle dient zur Konfiguration der moeglichen Realisierungszeitfenster

--CREATE TABLE T_BA_ZEITFENSTER (
--	ID number(9) not null,
--	ZEITFENSTER varchar(20) not null,
--	NIEDERLASSUNG_ID number(9) not null, 
--	ANZAHL_AUFTRAEGE number(9) not null,
--	MONTAG char(1) not null,
--	DIENSTAG char(1) not null,
--	MITTWOCH char(1) not null,
--	DONNERSTAG char(1) not null,
--	FREITAG char(1) not null,
--	SAMSTAG char(1) not null	
--);

--ALTER TABLE T_BA_ZEITFENSTER ADD CONSTRAINT PK_T_BA_ZEITFENSTER PRIMARY KEY (ID);
      
--ALTER TABLE T_BA_ZEITFENSTER
--  ADD CONSTRAINT FK_ZEITFENSTER_2_NIEDERLASSUNG
--      FOREIGN KEY (NIEDERLASSUNG_ID)
--      REFERENCES T_NIEDERLASSUNG (ID);
      
--GRANT SELECT ON T_BA_ZEITFENSTER TO R_HURRICAN_READ_ONLY;
--GRANT SELECT, INSERT, UPDATE ON T_BA_ZEITFENSTER TO R_HURRICAN_USER;

--create sequence S_T_BA_ZEITFENSTER_0 start with 10;
--grant select on S_T_BA_ZEITFENSTER_0 to public;

--insert into T_BA_ZEITFENSTER (ID, ZEITFENSTER, NIEDERLASSUNG_ID, ANZAHL_AUFTRAEGE) 
--values (1, '8-12', 3, 99, 1, 1, 1, 1, 1, 0);
--insert into T_BA_ZEITFENSTER (ID, ZEITFENSTER, NIEDERLASSUNG_ID, ANZAHL_AUFTRAEGE) 
--values (2, '12-16', 3, 99, 1, 1, 1, 1, 1, 0);
--insert into T_BA_ZEITFENSTER (ID, ZEITFENSTER, NIEDERLASSUNG_ID, ANZAHL_AUFTRAEGE) 
--values (3, '16-19', 3, 99, 0, 1, 0, 1, 0, 0);
--insert into T_BA_ZEITFENSTER (ID, ZEITFENSTER, NIEDERLASSUNG_ID, ANZAHL_AUFTRAEGE) 
--values (4, '8-12', 2, 99, 1, 1, 1, 1, 1, 0);
--insert into T_BA_ZEITFENSTER (ID, ZEITFENSTER, NIEDERLASSUNG_ID, ANZAHL_AUFTRAEGE) 
--values (5, '12-16', 2, 99, 1, 1, 1, 1, 1, 0);

--commit;


-- Tabelle T_EXT_SERVICE_PARTNER anlegen. 
-- Tabelle dient zur Verwaltung von externen Partnern, definiert in update_4_internWork

set define off;
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (15500, 'PARTNER_AUFTRAGSART', 'email', '1', 10, 'Auftragsart für externe Partner: Email');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (15501, 'PARTNER_AUFTRAGSART', 'manuell', '1', 20, 'Auftragsart für externe Partner: manuell');

insert into T_EXT_SERVICE_PROVIDER (ID, NAME, FIRSTNAME, STREET, HOUSE_NUM, POSTAL_CODE, CITY, PHONE, MAIL, FAX, CONTACT_TYPE) 
	values (19, 'com.patible', 'Borrmann & Sperlich GbR', 'Hohenlindner Str.', '6', '85622', 'Feldkirchen', '089 30905383 0', 'andreas.gilg@m-net.de', '089 30905383 99', 15500);
insert into T_EXT_SERVICE_PROVIDER (ID, NAME, FIRSTNAME, STREET, HOUSE_NUM, POSTAL_CODE, CITY, PHONE, MAIL, FAX, CONTACT_TYPE) 
	values (20, 'LK-Systeme, Telekommunikationsanlagen u. -geräte', null, 'Dietersheimer Str.', '6', '85716', 'Unterschleißheim/Lohhof', '089 32158681', 'andreas.gilg@m-net.de', null, 15501);

select S_T_EXT_SERVICE_PROVIDER_0.nextval from dual;
select S_T_EXT_SERVICE_PROVIDER_0.nextval from dual;


-- Niederlassung in Tabelle t_auftrag_technik aufnehmen
ALTER TABLE T_AUFTRAG_TECHNIK ADD NIEDERLASSUNG_ID number(10);
      
ALTER TABLE T_AUFTRAG_TECHNIK
  ADD CONSTRAINT FK_AT_2_NIEDERLASSUNG
      FOREIGN KEY (NIEDERLASSUNG_ID)
      REFERENCES T_NIEDERLASSUNG(ID);
      
insert into T_ABTEILUNG (ID, TEXT, NIEDERLASSUNG_ID, RELEVANT_4_PROJ, RELEVANT_4_BA) values (12, 'Extern', null, 0, 1);

-- Verlauf
ALTER TABLE T_VERLAUF_ABTEILUNG ADD EXT_SERVICE_PROVIDER_ID number(10);
ALTER TABLE T_VERLAUF_ABTEILUNG ADD REALISIERUNGSDATUM date;
ALTER TABLE T_VERLAUF_ABTEILUNG ADD ZUSATZAUFWAND number(10);
comment on column T_VERLAUF_ABTEILUNG.ZUSATZAUFWAND 
   is 'Zurueckgemeldeter Zusatzaufwand einer Abteilung bzw. eines ext. Partners. (Ref. auf T_REFERENCE)';
ALTER TABLE T_VERLAUF_ABTEILUNG ADD NIEDERLASSUNG_ID number(10);

ALTER TABLE T_VERLAUF_ABTEILUNG
  ADD CONSTRAINT FK_VA_2_EXTPARTNER
      FOREIGN KEY (EXT_SERVICE_PROVIDER_ID)
      REFERENCES T_EXT_SERVICE_PROVIDER(ID);
      
ALTER TABLE T_VERLAUF_ABTEILUNG
  ADD CONSTRAINT FK_VA_2_REFERENCE
      FOREIGN KEY (ZUSATZAUFWAND)
      REFERENCES T_REFERENCE(ID);
      
ALTER TABLE T_VERLAUF_ABTEILUNG
  ADD CONSTRAINT FK_VA_2_NIEDERLASSUNG
      FOREIGN KEY (NIEDERLASSUNG_ID)
      REFERENCES T_NIEDERLASSUNG(ID);
 
ALTER TABLE T_VERLAUF_ABTEILUNG DROP CONSTRAINT UK_T_VERLAUF_ABTEILUNG;
DROP INDEX UK_T_VERLAUF_ABTEILUNG;
ALTER TABLE T_VERLAUF_ABTEILUNG ADD CONSTRAINT UK_T_VERLAUF_ABTEILUNG UNIQUE (VERLAUF_ID, ABTEILUNG_ID, NIEDERLASSUNG_ID);

UPDATE T_VERLAUF_ABTEILUNG SET NIEDERLASSUNG_ID = 1;
UPDATE T_VERLAUF_ABTEILUNG SET NIEDERLASSUNG_ID = 2 where ABTEILUNG_ID = 6;
UPDATE T_VERLAUF_ABTEILUNG SET ABTEILUNG_ID = 5 where NIEDERLASSUNG_ID = 2 AND ABTEILUNG_ID = 6;

--ALTER TABLE T_VERLAUF_ABTEILUNG MODIFY NIEDERLASSUNG_ID NUMBER(10) NOT NULL;
      
-- Niederlassung
ALTER TABLE T_NIEDERLASSUNG ADD PARENT number(10);
UPDATE T_NIEDERLASSUNG SET PARENT = 5 where not ID = 5;
UPDATE T_NIEDERLASSUNG SET PARENT = 1 where ID = 2;
insert into T_NIEDERLASSUNG(ID, TEXT) values (4, 'Nürnberg');
insert into T_NIEDERLASSUNG(ID, TEXT, PARENT) values (5, 'Zentral', 1);

-- Mapping Reseller <--> Niederlassung in T_REFERENCE aufnehmen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION)
  values (1100, 'RESELLER_2_NL', '100000009', 1, 0, 
  'Mapping Reseller-zu-Niederlassung; STR_VALUE=Reseller, INT_VALUE=Niederlassungs-ID');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION)
  values (1101, 'RESELLER_2_NL', '400000001', 2, 0, 
  'Mapping Reseller-zu-Niederlassung; STR_VALUE=Reseller, INT_VALUE=Niederlassungs-ID');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION)
  values (1102, 'RESELLER_2_NL', '100000081', 3, 0, 
  'Mapping Reseller-zu-Niederlassung; STR_VALUE=Reseller, INT_VALUE=Niederlassungs-ID');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION)
  values (1103, 'RESELLER_2_NL', '500000010', 4, 0, 
  'Mapping Reseller-zu-Niederlassung; STR_VALUE=Reseller, INT_VALUE=Niederlassungs-ID');


ALTER TABLE T_NIEDERLASSUNG ADD DISPO_TEAMPOSTFACH varchar2(255);
--UPDATE T_NIEDERLASSUNG SET DISPO_TEAMPOSTFACH = 'GilgAn@m-net.de';
UPDATE T_NIEDERLASSUNG SET DISPO_TEAMPOSTFACH = 'disponenten_agb@m-net.de';
UPDATE T_NIEDERLASSUNG SET DISPO_TEAMPOSTFACH = 'ZPDispositionTP@m-net.de' where ID=5;
ALTER TABLE T_NIEDERLASSUNG ADD DISPO_PHONE varchar2(255);
UPDATE T_NIEDERLASSUNG SET DISPO_PHONE = '089 45200 8562';

CREATE TABLE T_ABTEILUNG_2_NIEDERLASSUNG (
			ID number(10) not null,
			ABTEILUNG_ID number(10) not null,
			NIEDERLASSUNG_ID number(10) not null
);
			
ALTER TABLE T_ABTEILUNG_2_NIEDERLASSUNG ADD CONSTRAINT PK_T_ABT_2_NL PRIMARY KEY (ID);
ALTER TABLE T_ABTEILUNG_2_NIEDERLASSUNG ADD CONSTRAINT UK_T_ABT_2_NL UNIQUE (ABTEILUNG_ID, NIEDERLASSUNG_ID);
      
ALTER TABLE T_ABTEILUNG_2_NIEDERLASSUNG
  ADD CONSTRAINT FK_ABT2NL_2_NL
      FOREIGN KEY (NIEDERLASSUNG_ID)
      REFERENCES T_NIEDERLASSUNG (ID);

ALTER TABLE T_ABTEILUNG_2_NIEDERLASSUNG
  ADD CONSTRAINT FK_ABT2NL_2_ABT
      FOREIGN KEY (ABTEILUNG_ID)
      REFERENCES T_ABTEILUNG(ID);
      
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (1, 1, 1);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (2, 1, 2);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (3, 1, 3);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (4, 1, 4);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (5, 1, 5);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (6, 1, 7);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (7, 2, 5);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (8, 3, 1);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (9, 3, 2);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (10, 3, 3);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (11, 3, 4);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (12, 3, 5);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (13, 3, 7);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (14, 4, 1);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (15, 4, 2);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (16, 4, 3);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (17, 4, 4);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (19, 4, 7);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (20, 5, 4);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (21, 1, 12);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (22, 3, 12);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (23, 4, 12);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (24, 1, 11);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (25, 3, 11);
insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
	values (26, 4, 11);
	
create sequence S_T_ABT_2_NL_0 start with 50;
grant select on S_T_ABT_2_NL_0 to public;

GRANT SELECT ON T_ABTEILUNG_2_NIEDERLASSUNG TO R_HURRICAN_READ_ONLY;
GRANT SELECT, INSERT, UPDATE ON T_ABTEILUNG_2_NIEDERLASSUNG TO R_HURRICAN_USER;


-- Zusatzaufwaende
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (14500, 'ZUSATZ_AUFWAND', 'Rufnummernzuordnung', '1', 10, null);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (14501, 'ZUSATZ_AUFWAND', 'Installationsservice', '1', 20, null);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (14502, 'ZUSATZ_AUFWAND', 'Montage Anschlussdose', '1', 30, null);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (14503, 'ZUSATZ_AUFWAND', 'Rufnummernzuordnung + Installationsservice', '1', 40, null);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (14504, 'ZUSATZ_AUFWAND', 'Rufnummernzuordnung + Montage Anschlussdose', '1', 50, null);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (14505, 'ZUSATZ_AUFWAND', 'Installationsservice + Montage Anschlussdose', '1', 60, null);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
  values (14506, 'ZUSATZ_AUFWAND', 'Rufnummernzuordnung + Installationsservice + Montage Anschlussdose', '1', 70, null);

  
insert into T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE) 
	values ( 401, 'de.augustakom.hurrican.gui.auftrag.actions.ChangeBARealDateAMAction', 'ACTION', 'termin.verschieben', 'Termin verschieben', null, null, 33, 0, 1);
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	values (282, 401, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition') ;
delete from T_GUI_MAPPING where GUI_ID = 309; 
delete from T_GUI_DEFINITION where ID = 309;


update T_ABTEILUNG set TEXT = 'ST Connect' where id = 1;
update T_ABTEILUNG set TEXT = 'ST Online' where id = 2;
update T_ABTEILUNG set TEXT = 'ST Voice' where id = 3;
update T_ABTEILUNG set TEXT = 'FieldService' where id = 5;
update T_ABTEILUNG set TEXT = 'AM' where id = 7;

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (2013, 'verl.check.auftrag.niederlassung', 
	'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckNiederlassungCommand', 
	'VERLAUF_CHECK', 'Command prueft die Niederlassungs-Zuordnung des Auftrags');

drop sequence S_T_SERVICE_COMMAND_MAPPI_0;
create sequence S_T_SERVICE_COMMAND_MAPPI_0 start with 920;
grant select on S_T_SERVICE_COMMAND_MAPPI_0 to public;

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (880, 2013, 11, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (881, 2013, 12, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (882, 2013, 13, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (883, 2013, 14, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (884, 2013, 15, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (885, 2013, 16, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (886, 2013, 17, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (887, 2013, 18, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (888, 2013, 19, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (889, 2013, 20, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (890, 2013, 21, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (891, 2013, 22, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (892, 2013, 23, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (893, 2013, 24, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (894, 2013, 25, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (895, 2013, 26, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (896, 2013, 27, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (897, 2013, 28, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (898, 2013, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (899, 2013, 30, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (900, 2013, 31, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (901, 2013, 32, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (902, 2013, 33, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (903, 2013, 34, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
values (904, 2013, 44, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0);


-- Dispo fuer Niederlassung 'Zentral' einfuehren, wenn zentrale Dispo aktiv werden soll
--insert into T_ABTEILUNG_2_NIEDERLASSUNG(ID, NIEDERLASSUNG_ID, ABTEILUNG_ID) 
--	values (18, 4, 5);



-- AUTHENTICATION
-- Niederlassung in Authentication
CREATE OR REPLACE VIEW AUTHENTICATION.V_NIEDERLASSUNG (ID, TEXT, CONSTRAINT PK_NIEDERLASSUNG PRIMARY KEY (ID) DISABLE NOVALIDATE) 
AS select ID, TEXT from HURRICAN.T_NIEDERLASSUNG;
CREATE OR REPLACE VIEW AUTHENTICATION.V_EXT_SERVICE_PROVIDER (ID, NAME, CONSTRAINT PK_EXT_SERVICE_PROVIDER PRIMARY KEY (ID) DISABLE NOVALIDATE) 
AS select ID, NAME from HURRICAN.T_EXT_SERVICE_PROVIDER;

ALTER TABLE AUTHENTICATION.USERS ADD NIEDERLASSUNG_ID number(10);
ALTER TABLE AUTHENTICATION.USERS ADD EXT_SERVICE_PROVIDER_ID number(10);

insert into AUTHENTICATION.DEPARTMENT (ID, NAME, DESCRIPTION) values (18, 'Extern', 'Externer Dienstleister');

update AUTHENTICATION.USERS set NIEDERLASSUNG_ID = 1 where phone like '0821%';
update AUTHENTICATION.USERS set NIEDERLASSUNG_ID = 3 where phone like '089%';
update AUTHENTICATION.USERS set NIEDERLASSUNG_ID = 2 where phone like '0831%';
update AUTHENTICATION.USERS set NIEDERLASSUNG_ID = 4 where phone like '0911%';
update AUTHENTICATION.USERS set NIEDERLASSUNG_ID = 1 where phone like '0731%';

commit;


insert into T_PROD_2_PROD (ID, PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID, DESCRIPTION) 
values (1287, 513, 513, 5000, 1, null);
