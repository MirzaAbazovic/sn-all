--
-- Update-Script fuer VoIP
--

CREATE TABLE T_AUFTRAG_VOIP (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , AUFTRAG_ID INTEGER(9) NOT NULL
     , SIP_PASSWORD VARCHAR(10) NOT NULL
     , EG_MODE INTEGER(9)
     , IS_ACTIVE TINYINT(1)
     , USERW VARCHAR(25) NOT NULL
     , GUELTIG_VON DATE NOT NULL
     , GUELTIG_BIS DATE NOT NULL
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

ALTER TABLE T_AUFTRAG_VOIP
  ADD CONSTRAINT FK_AUFTRAGVOIP_2_AUFTRAG
      FOREIGN KEY (AUFTRAG_ID)
      REFERENCES t_auftrag (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
   
ALTER TABLE T_AUFTRAG_VOIP
  ADD CONSTRAINT FK_AUFTRAGVOIP_2_REF
      FOREIGN KEY (EG_MODE)
      REFERENCES t_reference (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
      
alter table T_PRODUKT add column DN_MOEGLICH BIT after BRAUCHT_DN;
update t_produkt set DN_MOEGLICH=1, MAX_DN_COUNT=2 where PROD_ID=440;

insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, DESCRIPTION,
	EWSD, SNAPSHOT_REL, STR_VALUE, PARAMETER, GUELTIG_VON, GUELTIG_BIS)
	values (110, 'VoIP Maxi Pur analog', 20010, 'VOIP', 'VoIP Leistung fuer Maxi analog', 1, 1, 
	'Maxi_VoIP', 'Wirknetz/MaxiPurVoIP/{0}/Niederlassung_{1}', '2007-06-01', '2200-01-01');

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
    values (1005, 'add.voip', 
    'de.augustakom.hurrican.service.cc.impl.command.leistung.CreateVoIPCommand', 
    'LS_ZUGANG', 'Command-Klasse, um notwendige Daten fuer VoIP zu erzeugen');
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
    values (1006, 'remove.voip', 
    'de.augustakom.hurrican.service.cc.impl.command.leistung.CancelVoIPCommand', 
    'LS_KUENDIGUNG', 'Command-Klasse, um VoIP-Daten zu kuendigen');

insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS) 
	values (1005, 110, 'de.augustakom.hurrican.model.cc.TechLeistung');
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS) 
	values (1006, 110, 'de.augustakom.hurrican.model.cc.TechLeistung');

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
    values (2010, 'verl.check.voip', 
    'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckVoIPCommand', 
    'VERLAUF_CHECK', 'Command-Klasse, um VoIP-Daten zu pruefen (Realisierung und Kuendigung)');

-- Provisioning-Name auf DN-Leistung angeben (entspricht dem XML-Tag fuer die VoIP-Schnittstelle)
alter table T_LEISTUNG_4_DN add column PROVISIONING_NAME VARCHAR(25) after LEISTUNG;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='CW' where ID=1;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='AGRU' where ID=2;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='BoB' where ID=3;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='CLIPNOSCR' where ID=4;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='CLIP' where ID=5;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='CLIR1' where ID=6;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='CLIR2' where ID=7;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='CLIRSUSP' where ID=8;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='COLP' where ID=9;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='COLR' where ID=10;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='COLRREQ' where ID=11;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='COLRSUSP' where ID=12;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='CONF3' where ID=13;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='AOCD' where ID=18;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='CUG' where ID=20;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='DIVIP' where ID=21;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='DIVEI' where ID=25;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='DIVBY' where ID=26;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='DIVDA' where ID=28;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='DIVI' where ID=29;
update T_LEISTUNG_4_DN set PROVISIONING_NAME='DIVCDE' where ID=55;
-- evtl. noch weitere konfigurieren

insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION) 
	values (4000, 'VOIP_EG_MODE', 'Modem', 0, 1, 'VoIP-Endgeraet im Modem-Modus');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION) 
	values (4001, 'VOIP_EG_MODE', 'Router', 1, 1, 'VoIP-Endgeraet im Router-Modus');
	
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID) values (440, 110);

-- VoIP-Panel den Produkten zuordnen (od. Produktgruppe?)
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (212, "de.augustakom.hurrican.gui.auftrag.AuftragVoIPPanel", "PANEL", 
   "auftrag.voip.panel", "VoIP", "Panel fuer die Anzeige der VoIP Daten", null, 50, 1);	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (212, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");  

-- TODO: DN-Leistungsbuendel definieren und dem Produkt zuordnen
insert into T_LEISTUNGSBUENDEL (NAME, BESCHREIBUNG) 
    values ('Maxi VoIP analog', 'Leistungsbuendel Maxi mit VoIP analog Option');

    

-- Typ vom Produkt-Mapping muss auf 'phone' geaendert werden - sonst koennen keine
-- Rufnummernleistungen gezogen werden!
update t_produkt_mapping set mapping_part_type='phone' where prod_id=440;


--
-- ACHTUNG: ServiceChains erst nach GUI-Verteilung aendern!!!
--

-- Verlauf-Chains erweitern
 -- Mnet-DSLonly-Check erweitern um "verl.check.voip"
delete from t_service_command_mapping where ref_id=29;
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2009, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1);
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2000, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2);
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2001, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3);
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2007, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 4);
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2004, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5);
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2005, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6);
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2006, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 7);		
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2010, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 8);
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2008, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 9);

	
 -- neue Chain mit "verl.check.short.term" und "verl.check.voip" erstellen und MaxiPur zuordnen
delete from t_service_chain where NAME='VoIP_with_ShortTerm_Check';
insert into t_service_chain (NAME, TYPE) values ('VoIP_with_ShortTerm_Check', 'VERLAUF_CHECK');
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2010, ?, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1);
insert into t_service_command_mapping (command_id, ref_id, REF_CLASS, ORDER_NO) 
	values (2008, ?, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2);

--
-- AUTHENTICATION
--
-- TEST
INSERT INTO db (ID, NAME, DRIVER, URL, SCHEMA, DESCRIPTION, HIBERNATE_DIALECT) 
	VALUES (11, 'production', 'oracle.jdbc.driver.OracleDriver', 
	'jdbc:oracle:thin:@192.168.229.23:1521:TEST01', 'MNETCALL', 'Definition fuer die Production-Datenbank',
	'net.sf.hibernate.dialect.Oracle9Dialect');
-- PRODUKTIV
INSERT INTO db (ID, NAME, DRIVER, URL, SCHEMA, DESCRIPTION, HIBERNATE_DIALECT) 
	VALUES (11, 'production', 'oracle.jdbc.driver.OracleDriver', 
	'jdbc:oracle:thin:@192.168.229.23:1521:PROD01', 'MNETCALL', 'Definition fuer die Production-Datenbank',
	'net.sf.hibernate.dialect.Oracle9Dialect');
INSERT INTO account (APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, DESCRIPTION, DB_ID) 
	VALUES (1, 'production.default', 'ORA_HURRICAN', 'w29L68o5E/h6qXz2KaGTMA==', 'DB-Account fuer die PRODUCTION DB', 11);

-- Account den EWSD-Usern zuordnen
-- ? durch ID des generierten Accounts ersetzen
insert into useraccount (user_id, account_id, db_id) values (21, 23, 11);
insert into useraccount (user_id, account_id, db_id) values (62, 23, 11);
insert into useraccount (user_id, account_id, db_id) values (76, 23, 11);
insert into useraccount (user_id, account_id, db_id) values (82, 23, 11);
insert into useraccount (user_id, account_id, db_id) values (83, 23, 11);
-- ITS und UnitTest zuordnen
insert into useraccount (user_id, account_id, db_id) values (1, 23, 11);
insert into useraccount (user_id, account_id, db_id) values (2, 23, 11);
insert into useraccount (user_id, account_id, db_id) values (3, 23, 11);
insert into useraccount (user_id, account_id, db_id) values (4, 23, 11);
insert into useraccount (user_id, account_id, db_id) values (109, 23, 11);


