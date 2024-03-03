--
-- SQL-Script fuer die Erweiterungen fuer QoS
--

CREATE TABLE T_AUFTRAG_QOS (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , AUFTRAG_ID INTEGER(9) NOT NULL
     , QOS_CLASS_REF_ID INTEGER(9) NOT NULL
     , PERCENTAGE INTEGER(3) NOT NULL
     , GUELTIG_VON DATE NOT NULL
     , GUELTIG_BIS DATE NOT NULL default '2200-01-01'
     , USERW VARCHAR(30) NOT NULL
     , DATEW TIMESTAMP
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

ALTER TABLE T_AUFTRAG_QOS
  ADD CONSTRAINT FK_AUFTRAGQOS_2_AUFTRAG
      FOREIGN KEY (AUFTRAG_ID)
      REFERENCES t_auftrag (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
   
ALTER TABLE T_AUFTRAG_QOS
  ADD CONSTRAINT FK_AUFTRAGQOS_2_REF
      FOREIGN KEY (QOS_CLASS_REF_ID)
      REFERENCES t_reference (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

-- unterschiedliche QoS-Klassen als Reference definieren
delete from T_REFERENCE WHERE TYPE='QOS_CLASS';
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, UNIT_ID, GUI_VISIBLE, DESCRIPTION, ORDER_NO) 
	values (10000, 'QOS_CLASS', 'Class 1 - Realtime', 33, 100, 1, 
	'QoS Klasse 1 - Default Wert in INT_VALUE definiert', 100);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, UNIT_ID, GUI_VISIBLE, DESCRIPTION, ORDER_NO) 
	values (10001, 'QOS_CLASS', 'Class 2 - Voice Signaling', 5, 100, 1, 
	'QoS Klasse 2 - Default Wert in INT_VALUE definiert', 110);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, UNIT_ID, GUI_VISIBLE, DESCRIPTION, ORDER_NO) 
	values (10002, 'QOS_CLASS', 'Class 3 - Critical Data', 36, 100, 1, 
	'QoS Klasse 3 - Default Wert in INT_VALUE definiert', 120);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, UNIT_ID, GUI_VISIBLE, DESCRIPTION, ORDER_NO) 
	values (10003, 'QOS_CLASS', 'Class 4 - Best Effort', 26, 100, 1, 
	'QoS Klasse 4 - Default Wert in INT_VALUE definiert', 130);

-- technische Leistung fuer QoS anlegen
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, DESCRIPTION, 
	DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS)
    values (50, 'Quality-of-Service - QoS', 10050, 'QOS', 'Marker-Leistung fuer QoS',
    0, 0, 1, 0, 0, 1, '2008-02-01', '2200-01-01');
    
-- QoS-Leistung den einzelnen Produkten zuordnen
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (440, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (441, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (442, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (400, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (410, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (411, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (420, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (421, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (430, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (431, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (312, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (313, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (300, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (301, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (302, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (303, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (304, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (305, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (60, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (61, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (62, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (63, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (64, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (66, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (67, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (51, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (52, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (53, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (54, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (16, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (17, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (18, 50, 0);
insert into T_PROD_2_TECH_LEISTUNG (PROD_ID, TECH_LS_ID, IS_DEFAULT) values (11, 50, 0);

-- ServiceCommands anlegen und Mapping erstellen
insert into t_service_commands (ID, NAME, CLASS, TYPE, DESCRIPTION) 
	values (1007, 'add.qos',
	'de.augustakom.hurrican.service.cc.impl.command.leistung.CreateQoSCommand',
	'LS_ZUGANG', 'Command-Klasse, um die Default-Daten von QoS einem Auftrag zuzuordnen');
insert into t_service_command_mapping (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
	values (1007, 50, 'de.augustakom.hurrican.model.cc.TechLeistung', null);

insert into t_service_commands (ID, NAME, CLASS, TYPE, DESCRIPTION) 
	values (1008, 'remove.qos',
	'de.augustakom.hurrican.service.cc.impl.command.leistung.CancelQoSCommand',
	'LS_KUENDIGUNG', 'Command-Klasse, um die QoS-Daten auf einem Auftrag zu deaktivieren');
insert into t_service_command_mapping (COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) 
	values (1008, 50, 'de.augustakom.hurrican.model.cc.TechLeistung', null);

-- Definition vom QoS-Panel
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (213, "de.augustakom.hurrican.gui.auftrag.AuftragQoSPanel", "PANEL", 
   "auftrag.qos.panel", "QoS", "Panel fuer die Anzeige der Quality-of-Service Daten", null, 60, 1);	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (213, 1, "de.augustakom.hurrican.model.cc.ProduktGruppe"),
	       (213, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 3, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 4, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 5, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 6, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 8, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 9, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 12, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 16, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 18, "de.augustakom.hurrican.model.cc.ProduktGruppe");


