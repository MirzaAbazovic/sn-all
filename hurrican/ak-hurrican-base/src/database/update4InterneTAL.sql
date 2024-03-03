--
-- Update-Script, um die notwendigen DB-Struktur fuer die 
-- interne TAL-Bestellung zu erstellen.
--

CREATE TABLE T_CB_VORGANG (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , CB_ID INTEGER(9) NOT NULL
     , AUFTRAG_ID INTEGER(9) NOT NULL
     , CARRIER_ID INTEGER(9) NOT NULL
     , TYP INTEGER(9) NOT NULL
     , USECASE_ID INTEGER(9)
     , STATUS INTEGER(9) NOT NULL
     , BEZEICHNUNG_MNET VARCHAR(50)
     , VORGABE_MNET DATE NOT NULL
     , BEMERKUNG_MNET VARCHAR(255)
     , RET_OK BIT
     , RET_LBZ VARCHAR(30)
     , RET_VTRNR VARCHAR(25)
     , RET_AQS VARCHAR(70)
     , RET_LL VARCHAR(70)
     , RET_KUNDE_VOR_ORT BIT
     , RET_REAL_DATE DATE
     , RET_BEMERKUNG VARCHAR(255)
     , SUBMITTED_AT DATE
     , ANSWERED_AT DATE
     , USER_ID INTEGER(6) NOT NULL
     , BEARBEITER VARCHAR(50)
     , CARRIER_BEARBEITER VARCHAR(100)
	 , CARRIER_KENNUNG_ABS VARCHAR(45)
	 , EXM_ID INTEGER(11)
	 , EXM_RET_FEHLERTYP LONG(11)
     , PRIMARY KEY (ID)
)ENGINE=InnoDB;

alter table T_CB_VORGANG add column CARRIER_REF_NR varchar(25) after BEMERKUNG_MNET;

ALTER TABLE T_CARRIER_KENNUNG add column EL_TAL_ABSENDER_ID VARCHAR(45);
update T_CARRIER_KENNUNG set EL_TAL_ABSENDER_ID='D043-082' where ID=1;
update T_CARRIER_KENNUNG set EL_TAL_ABSENDER_ID='D043-082' where ID=2;

ALTER TABLE T_CARRIER add column EL_TAL_EMPF_ID VARCHAR(45);
update T_CARRIER set EL_TAL_EMPF_ID='D043-082' where id=4;
update T_CARRIER set EL_TAL_EMPF_ID='D001-033' where id=12;
update T_CARRIER set EL_TAL_EMPF_ID='D052-089' where id=17;

CREATE TABLE T_CB_USECASE (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , REFERENCE_ID INTEGER(9) NOT NULL
     , EXM_TBV_ID INTEGER(11) NOT NULL
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_CB_CONFIG (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , USECASE_ID INTEGER(9) NOT NULL
     , BEZEICHNUNG VARCHAR(50) NOT NULL
     , POSITION INTEGER(9) NOT NULL
     , COMMAND_ID INTEGER(9) NOT NULL
     , MIN INTEGER(9) NOT NULL
     , MAX INTEGER(9) NOT NULL
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_PRODUKT_DTAG(
		ID INT(9) NOT NULL AUTO_INCREMENT
	,	RANG_SS_TYPE VARCHAR(10) NOT NULL
	,	PRODUKT_DTAG INT(9)
	,	B010_2 INT(9)
	,	B010_3 VARCHAR(1)
	,	B010_4 VARCHAR(1)
	,   PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_CB_USECASE_2_CARRIER_KNG (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , USECASE_ID INTEGER(9) NOT NULL
     , CARRIERKENNUNG_ID INTEGER(9) NOT NULL
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

ALTER TABLE T_CB_CONFIG
  ADD CONSTRAINT FK_CBCONFIG_2_USECASE
      FOREIGN KEY (USECASE_ID)
      REFERENCES T_CB_USECASE (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_CB_CONFIG
  ADD CONSTRAINT FK_CBCONFIG_2_CMD
      FOREIGN KEY (COMMAND_ID)
      REFERENCES t_service_commands (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_CB_USECASE
  ADD CONSTRAINT FK_CBUSECASE_2_REF
      FOREIGN KEY (REFERENCE_ID)
      REFERENCES t_reference (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
   
ALTER TABLE T_CB_VORGANG
  ADD CONSTRAINT FK_CBVORGANG_2_USEC
      FOREIGN KEY (USECASE_ID)
      REFERENCES T_CB_USECASE (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;  
   
ALTER TABLE T_CB_VORGANG
  ADD CONSTRAINT FK_CB_VORGANG_2_CB
      FOREIGN KEY (CB_ID)
      REFERENCES t_carrierbestellung (CB_ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
   
ALTER TABLE T_CB_VORGANG
  ADD CONSTRAINT FK_CBVORGANG_2_AUFTRAG
      FOREIGN KEY (AUFTRAG_ID)
      REFERENCES T_AUFTRAG (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_CB_VORGANG
  ADD CONSTRAINT FK_CB_VORGANG_2_REF_TYP
      FOREIGN KEY (TYP)
      REFERENCES t_reference (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_CB_VORGANG
  ADD CONSTRAINT FK_CB_VORGANG_2_REF_STATUS
      FOREIGN KEY (STATUS)
      REFERENCES t_reference (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_CB_VORGANG
  ADD CONSTRAINT FK_CB_VORGANG_2_CARRIER
      FOREIGN KEY (CARRIER_ID)
      REFERENCES t_carrier (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_CB_USECASE_2_CARRIER_KNG
  ADD CONSTRAINT FK_CBUC_2_CKNG
      FOREIGN KEY (CARRIERKENNUNG_ID)
      REFERENCES t_niederlassung (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_CB_USECASE_2_CARRIER_KNG
  ADD CONSTRAINT FK_CBUC_2_UC
      FOREIGN KEY (USECASE_ID)
      REFERENCES T_CB_USECASE (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
   
alter table T_REFERENCE change TYPE TYPE VARCHAR(30);

# -- t_produkt_dtag
insert into t_produkt_dtag (ID, RANG_SS_TYPE, PRODUKT_DTAG, B010_2, B010_3, B010_4) values (1, '2N',1, 2, "N", "N" );
insert into t_produkt_dtag (ID, RANG_SS_TYPE, PRODUKT_DTAG, B010_2, B010_3, B010_4) values (2, '2H',2, 2, "J", "N" );
insert into t_produkt_dtag (ID, RANG_SS_TYPE, PRODUKT_DTAG, B010_2, B010_3, B010_4) values (3, '4N',3, 4, "N", "N" );
insert into t_produkt_dtag (ID, RANG_SS_TYPE, PRODUKT_DTAG, B010_2, B010_3, B010_4) values (4, '4H',4, 4, "J", "N" );
insert into t_produkt_dtag (ID, RANG_SS_TYPE, PRODUKT_DTAG, B010_2, B010_3, B010_4) values (5, '4ZWR',6, 4, "N", "J" );
insert into t_produkt_dtag (ID, RANG_SS_TYPE, PRODUKT_DTAG, B010_2, B010_3, B010_4) values (6, 'LWL',7, 2, "N", "N" );

# -- t_service_commands
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4050, 'tal.data.b001', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB001Command', 'TAL_BESTELLUNG_DATA', 'Liefert die Auftragsdaten fuer die el.Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4051, 'tal.data.b002', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB002Command', 'TAL_BESTELLUNG_DATA', 'Liefert die Netzbetreiberdaten fuer die el.Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4052, 'tal.data.b003', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB003Command', 'TAL_BESTELLUNG_DATA', 'Liefert die Rufnummerndaten fuer die el.Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4053, 'tal.data.b004', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB004Command', 'TAL_BESTELLUNG_DATA', 'Liefert die Adressdaten fuer die el.Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4054, 'tal.data.b006', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB006Command', 'TAL_BESTELLUNG_DATA', 'Liefert die Zusatzdaten Portierung fuer die el.Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4055, 'tal.data.b007', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB007Command', 'TAL_BESTELLUNG_DATA', 'Liefert die Lage der TAE-Daten fuer die el.Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4056, 'tal.data.b008', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB008Command', 'TAL_BESTELLUNG_DATA', 'Liefert die Anschlusskuendigung fuer die el.Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4057, 'tal.data.b009', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB009Command', 'TAL_BESTELLUNG_DATA', 'Liefert die Vertragsdaten fuer die el.Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4058, 'tal.data.physik', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataPhysikCommand', 'TAL_BESTELLUNG_DATA',	'Liefert die Physikdaten fuer die Segmente B010 und B011');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4059, 'tal.data.b014', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB014Command', 'TAL_BESTELLUNG_DATA',	'Liefert die UEVT-Standort fuer die el. Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4060, 'tal.data.b015', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB015Command', 'TAL_BESTELLUNG_DATA',	'Liefert die Bearbeiterdaten fuer die el. Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4061, 'tal.data.b017', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB017Command', 'TAL_BESTELLUNG_DATA',	'Liefert die Sonstiges-Daten fuer die el. Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4062, 'tal.data.b016', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB016Command', 'TAL_BESTELLUNG_DATA',	'Liefert die Auftragsklammer fuer die el. Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4063, 'tal.data.b021', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB021Command', 'TAL_BESTELLUNG_DATA',	'Liefert die Zusatzinformationen fuer die el. Tal Dtag');
insert into t_service_commands (ID, NAME, CLASS, Type, DESCRIPTION) values ( 4064, 'tal.data.b022', 'de.augustakom.hurrican.service.cc.impl.command.tal.GetDataB022Command', 'TAL_BESTELLUNG_DATA',	'Liefert die HVT-Karussel fuer die el. Tal Dtag');


-- Grundsaetzliche Arten der TAL-Bestellung definieren
delete from t_reference where id in (8000,8001,8002,8003);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION, ORDER_NO) 
	values (8000, 'TAL_BESTELLUNG_TYP', 'Neubestellung', 40, 1, 'Typ fuer eine TAL-Bestellung - Vorgang: Neubestellung; INT_VALUE = ID der ServiceChain', 100);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION, ORDER_NO) 
	values (8001, 'TAL_BESTELLUNG_TYP', 'Kuendigung', 41, 1, 'Typ fuer eine TAL-Bestellung - Vorgang: Kuendigung; INT_VALUE = ID der ServiceChain', 800);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION, ORDER_NO) 
	values (8002, 'TAL_BESTELLUNG_TYP', 'Storno', 42, 1, 'Typ, um eine TAL-Bestellung zu stornieren; INT_VALUE = ID der ServiceChain', 900);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, DESCRIPTION, ORDER_NO) 
	values (8003, 'TAL_BESTELLUNG_TYP', 'Nutzungsänderung', 43, 1, 'Typ, fuer eine TAL-Bestellung - Vorgang: Nutzungsaenderung; INT_VALUE = ID der ServiceChain', 200);


-- Usecases von DTAG zum Bestellungstyp definieren
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (1, 8000, 7);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (2, 8000, 8);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (3, 8000, 9);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (4, 8000, 10);

insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (5, 8001, 43);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (6, 8001, 44);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (7, 8001, 45);

insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (8, 8002, 11);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (9, 8002, 12);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (10, 8002, 13);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (11, 8002, 14);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (12, 8002, 55);
insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (13, 8002, 81);

insert into T_CB_USECASE (ID, REFERENCE_ID, EXM_TBV_ID) values (14, 8003, 54);

-- Mapping zwischen CB-Usecase und CarrierKennung erstellen
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (1, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (2, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (3, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (4, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (5, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (6, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (7, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (8, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (9, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (10, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (11, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (12, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (13, 1);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (14, 1);

insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (2, 2);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (3, 2);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (4, 2);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (6, 2);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (9, 2);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (10, 2);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (12, 2);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (13, 2);
insert into t_cb_usecase_2_carrier_kng (USECASE_ID, CARRIERKENNUNG_ID) values (14, 2);

INSERT INTO `t_cb_config` VALUES 
(1,1,'B001',1,4050,1,1),
(2,1,'B016',2,4062,0,1),
(3,1,'B002',3,4051,1,1),
(4,1,'B003',4,4052,1,20),
(5,1,'B004',5,4053,1,20),
(6,1,'B006',6,4054,1,1),
(7,1,'B008',7,4055,1,1),
(8,1,'B015',8,4060,1,1),
(9,1,'B017',9,4061,0,1),
(10,1,'B021',10,4063,0,99),
(11,2,'B001',1,4050,1,1),
(12,2,'B016',2,4062,0,1),
(13,2,'B002',3,4051,1,1),
(14,2,'B004',4,4053,1,1),
(15,2,'B007',5,4055,1,1),
(16,2,'physikCommand',6,4058,1,1),
(17,2,'B014',7,4059,1,1),
(18,2,'B015',8,4060,1,1),
(19,2,'B017',9,4061,0,1),
(20,2,'B021',10,4063,0,99),
(21,2,'B022',11,4064,0,1),
(22,4,'B001',1,4050,1,1),
(23,4,'B016',2,4062,0,1),
(24,4,'B002',3,4051,1,1),
(25,4,'B003',4,4052,1,20),
(26,4,'B004',5,4053,1,20),
(27,4,'B007',6,4055,1,1),
(28,4,'B006',7,4054,1,1),
(29,4,'B008',8,4055,1,1),
(30,4,'physikCommand',9,4058,1,1),
(31,4,'B014',10,4059,1,1),
(32,4,'B015',11,4060,1,1),
(33,4,'B017',12,4061,0,1),
(34,4,'B021',13,4063,0,99),
(35,4,'B022',14,4064,0,1);



-- 8100 - 8199  Stati fuer eingestellt, aber nicht uebertragen
delete from t_reference where id in (8100,8200,8300,8400,8499);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, DESCRIPTION) 
	values (8100, 'TAL_BESTELLUNG_STATUS', 'submitted', 1, 'TAL-Bestellung neu eingestellt - noch nicht an Carrier uebergeben');
-- 8200 - 8299  Stati fuer 'an Carrier uebermittelt'
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, DESCRIPTION) 
	values (8200, 'TAL_BESTELLUNG_STATUS', 'transferred', 1, 'TAL-Bestellung an Carrier uebertragen - Bearbeitung von Carrier ausstehend');
-- 8300 - 8399  Stati fuer Rueckmeldungen von Carrier
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, DESCRIPTION) 
	values (8300, 'TAL_BESTELLUNG_STATUS', 'answered', 1, 'Carrier hat TAL-Bestellung bearbeitet und zurueck geliefert');
-- 8400 - 8499  Abschluss-Stati des Vorgangs
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, DESCRIPTION) 
	values (8400, 'TAL_BESTELLUNG_STATUS', 'closed', 1, 'Vorgang ist komplett abgeschlossen');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, DESCRIPTION) 
	values (8499, 'TAL_BESTELLUNG_STATUS', 'cancelled', 1, 'Vorgang wurde storniert');
	
--
-- Check-Commands (bzw. Chains) zu den Typen hinterlegen.
delete from t_service_commands where id in (4000, 4001, 4002, 4003, 4004, 4005);
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
  values (4000, 'check.act.cbvorgang', 'de.augustakom.hurrican.service.cc.impl.command.tal.CheckActiveCbvTALCommand', 
  'TAL_BESTELLUNG_CHECK', 'Command prueft, ob zu der Carrierbestellung noch ein aktiver el. Verlauf vorhanden ist.');
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
  values (4001, 'check.cb.exist', 'de.augustakom.hurrican.service.cc.impl.command.tal.CheckCBExistTALCommand', 
  'TAL_BESTELLUNG_CHECK', 'Command prueft, ob die angegebene Carrierbestellung vorhanden ist.');
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
  values (4002, 'check.cb4neu', 'de.augustakom.hurrican.service.cc.impl.command.tal.Check4NeuTALCommand', 
  'TAL_BESTELLUNG_CHECK', 'Command, um Daten der Carrierbestellung fuer Neubestellungen zu pruefen');
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
  values (4003, 'check.cb4kuendigung', 'de.augustakom.hurrican.service.cc.impl.command.tal.Check4KuendigungTALCommand', 
  'TAL_BESTELLUNG_CHECK', 'Command, um Daten der Carrierbestellung fuer eine TAL-Kuendigung zu pruefen');
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
  values (4004, 'check.carrier', 'de.augustakom.hurrican.service.cc.impl.command.tal.CheckCarrierTALCommand', 
  'TAL_BESTELLUNG_CHECK', 'Command um zu pruefen, ob der Carrier fuer el. TAL-Bestellung zugelassen ist.');
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
  values (4005, 'check.accesspoint.address', 'de.augustakom.hurrican.service.cc.impl.command.tal.CheckAPAddressTALCommand', 
  'TAL_BESTELLUNG_CHECK', 'Command um zu pruefen, eine Standortadresse angegeben ist.');

-- ServiceChains aufbauen
delete from t_service_command_mapping where ref_id=40;
delete from t_service_chain where id=40;
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
    values (40, 'TAL-Neubestellung', 'TAL_BESTELLUNG_CHECK', 'Chain um die Daten fuer eine TAL-Neubestellung zu pruefen');
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4001, 40, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4000, 40, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4004, 40, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4002, 40, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 4);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4005, 40, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5);
    
delete from t_service_command_mapping where ref_id=41;
delete from t_service_chain where id=41;
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
    values (41, 'TAL-Kuendigung', 'TAL_BESTELLUNG_CHECK', 'Chain um die Daten fuer eine TAL-Kuendigung zu pruefen');
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4001, 41, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4000, 41, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4004, 41, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4003, 41, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 4);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4005, 41, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5);

delete from t_service_command_mapping where ref_id=42;
delete from t_service_chain where id=42;    
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
    values (42, 'TAL-Storno', 'TAL_BESTELLUNG_CHECK', 'Chain um die Daten fuer eine TAL-Stornierung zu pruefen');
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4001, 42, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4000, 42, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4004, 42, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4005, 42, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5);

delete from t_service_command_mapping where ref_id=43;
delete from t_service_chain where id=43;
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
    values (43, 'TAL-Nutzungsaenderung', 'TAL_BESTELLUNG_CHECK', 'Chain um die Daten fuer eine TAL-Nutzungsaenderung zu pruefen');
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4001, 43, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4000, 43, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4004, 43, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4002, 43, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 4);
insert into T_SERVICE_COMMAND_MAPPING (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
    values (4005, 43, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5);

--
-- AUTHENTICATION
--
-- TEST
INSERT INTO db (ID, NAME, DRIVER, URL, SCHEMA, DESCRIPTION, HIBERNATE_DIALECT) 
	VALUES (12, 'tal', 'oracle.jdbc.driver.OracleDriver', 
	'jdbc:oracle:thin:@192.168.229.23:1521:TEST01', 'MNETCALL', 'Definition fuer die TAL-Datenbank',
	'net.sf.hibernate.dialect.Oracle9Dialect');
-- PRODUKTIV
INSERT INTO db (ID, NAME, DRIVER, URL, SCHEMA, DESCRIPTION, HIBERNATE_DIALECT) 
	VALUES (12, 'tal', 'oracle.jdbc.driver.OracleDriver', 
	'jdbc:oracle:thin:@192.168.229.23:1521:PROD01', 'MNETCALL', 'Definition fuer die TAL-Datenbank',
	'net.sf.hibernate.dialect.Oracle9Dialect');
INSERT INTO account (APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, DESCRIPTION, DB_ID) 
	VALUES (1, 'tal.default', 'ORA_HURRICAN', 'w29L68o5E/h6qXz2KaGTMA==', 'DB-Account fuer die TAL DB', 12);
INSERT INTO account (APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD, DESCRIPTION, DB_ID) 
	VALUES (2, 'scheduler.tal.default', 'ORA_HURRICAN', 'w29L68o5E/h6qXz2KaGTMA==', 'DB-Account fuer die TAL DB', 12);

-- Account den EWSD-Usern zuordnen
-- ITS und UnitTest zuordnen
insert into useraccount (user_id, account_id, db_id) values (1, 24, 12);
insert into useraccount (user_id, account_id, db_id) values (2, 24, 12);
insert into useraccount (user_id, account_id, db_id) values (3, 24, 12);
insert into useraccount (user_id, account_id, db_id) values (4, 24, 12);
insert into useraccount (user_id, account_id, db_id) values (109, 24, 12);
-- Account dem Scheduler zuordnen
insert into useraccount (user_id, account_id, db_id) values (110, 25, 12);
-- TODO SCV-Usern den Account zuordnen

