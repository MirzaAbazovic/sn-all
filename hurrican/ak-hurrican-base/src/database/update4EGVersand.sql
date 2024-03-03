alter table t_eg_2_auftrag add column VERSANDART int(9) after MONTAGEART;
alter table t_eg_2_auftrag add column LIEFERADRESSE_ID int(11) after VERSANDART;
alter table t_eg_2_auftrag add column REF_ID_VERSAND_STATUS int(9) after LIEFERADRESSE_ID;
alter table t_eg_2_auftrag add column LIEFERSCHEIN_ID int(11) after REF_ID_VERSAND_STATUS;
alter table t_eg_2_auftrag add column BEMERKUNG varchar(255) after LIEFERSCHEIN_ID;

ALTER TABLE t_eg_2_auftrag 
  ADD CONSTRAINT FK_EG2AUFTRAG_2_LIEFERSCHEIN
      FOREIGN KEY (LIEFERSCHEIN_ID)
      REFERENCES t_lieferschein (ID);

ALTER TABLE t_eg_2_auftrag 
  ADD CONSTRAINT FK_EG2AUFTRAG_2_ADDRESS
      FOREIGN KEY (LIEFERADRESSE_ID)
      REFERENCES t_address (ID);
      
ALTER TABLE t_eg_2_auftrag 
  ADD CONSTRAINT FK_EG2AUFTRAG_2_REFERENCE
      FOREIGN KEY (REF_ID_VERSAND_STATUS)
      REFERENCES t_reference (ID);

ALTER TABLE t_eg_2_auftrag 
  ADD CONSTRAINT FK_EG2AUFTRAG_2_REFERENCE_2
      FOREIGN KEY (VERSANDART)
      REFERENCES t_reference (ID);
      
      
      
-- Versand-Grund
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (600, 'EG_VERSAND_GRUND', 'Adressfehler (11)', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (601, 'EG_VERSAND_GRUND', 'Annahme verweigert (15)', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (602, 'EG_VERSAND_GRUND', 'Nicht angetroffen (42)', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (603, 'EG_VERSAND_GRUND', 'Gerät defekt', true);
-- Versand-Status
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (650, 'EG_VERSAND_STATUS', 'Retoure', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (651, 'EG_VERSAND_STATUS', 'Storno', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (652, 'EG_VERSAND_STATUS', 'Erneuter Versand', true);
-- Lieferschein-Stati
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (700, 'LIEFERSCHEIN_STATUS', 'Wartet', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (701, 'LIEFERSCHEIN_STATUS', 'Gedruckt', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (702, 'LIEFERSCHEIN_STATUS', 'Versand', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (703, 'LIEFERSCHEIN_STATUS', 'Abgeschlossen', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (704, 'LIEFERSCHEIN_STATUS', 'Retoure', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (705, 'LIEFERSCHEIN_STATUS', 'Storno', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (699, 'LIEFERSCHEIN_STATUS', 'Undefined', true);

create table t_lieferschein (
	ID INTEGER(11) NOT NULL AUTO_INCREMENT,
	PREFIX VARCHAR(1),
    REQUEST_ID INTEGER(11),
    STATUS_ID INTEGER(9) NOT NULL,
    PRINTED_AT DATETIME,
    RETOURE_GRUND_ID INTEGER(9),
    BEARBEITER VARCHAR(100),
    PRIMARY KEY(ID) );
     
ALTER TABLE t_lieferschein 
  ADD CONSTRAINT FK_LIEFERSCHEIN_2_REFERENCE_1
      FOREIGN KEY (STATUS_ID)
      REFERENCES t_reference (ID);

ALTER TABLE t_lieferschein 
  ADD CONSTRAINT FK_LIEFERSCHEIN_2_REPORTREQUEST
      FOREIGN KEY (REQUEST_ID)
      REFERENCES t_report_request (ID);

ALTER TABLE t_lieferschein 
  ADD CONSTRAINT FK_LIEFERSCHEIN_2_REFERENCE_2
      FOREIGN KEY (RETOURE_GRUND_ID)
      REFERENCES t_reference (ID);
      
insert into t_service_commands (
ID, NAME, CLASS, TYPE, DESCRIPTION) values (
3800, 'lieferschein.change.status.gedruckt',
'de.augustakom.hurrican.service.cc.impl.command.lieferschein.ChangeLieferscheinStatusGedrucktCommand',
'LIEFERSCHEIN', 'Ändert Status des Lieferscheins');

insert into t_service_command_mapping ( 
COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) values (
3800, 701, 'de.augustakom.hurrican.model.cc.Reference', 1);

insert into t_service_commands (
ID, NAME, CLASS, TYPE, DESCRIPTION) values (
3801, 'lieferschein.change.status.retoure',
'de.augustakom.hurrican.service.cc.impl.command.lieferschein.ChangeLieferscheinStatusRetoureCommand',
'LIEFERSCHEIN', 'Ändert Status des Lieferscheins');

insert into t_service_command_mapping ( 
COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) values (
3801, 704, 'de.augustakom.hurrican.model.cc.Reference', 1);
   
insert into t_service_commands (
ID, NAME, CLASS, TYPE, DESCRIPTION) values (
3802, 'lieferschein.change.status.storno',
'de.augustakom.hurrican.service.cc.impl.command.lieferschein.ChangeLieferscheinStatusStornoCommand',
'LIEFERSCHEIN', 'Ändert Status des Lieferscheins');

insert into t_service_command_mapping ( 
COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) values (
3802, 705, 'de.augustakom.hurrican.model.cc.Reference', 1);

insert into t_service_commands (
ID, NAME, CLASS, TYPE, DESCRIPTION) values (
3803, 'lieferschein.change.status.abgeschlossen',
'de.augustakom.hurrican.service.cc.impl.command.lieferschein.ChangeLieferscheinStatusAbgeschlossenCommand',
'LIEFERSCHEIN', 'Ändert Status des Lieferscheins');

insert into t_service_command_mapping ( 
COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO) values (
3803, 703, 'de.augustakom.hurrican.model.cc.Reference', 1);
   
insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION) 
values ( 200, 'eg.versand.status.file', 'T:\\Special-Transfers\\Versand\\AGB\\LMF\\', 'Pfad für Excel-File mit Tracking-Nummern');
insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION) 
values ( 201, 'eg.versand.archiv', 'T:\\Special-Transfers\\Versand\\AGB\\LMF\\done\\', 'Archiv für Excel-Files');
insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION) 
values ( 202, 'eg.versand.email.lager', 'LagerAgbTP@m-net.de', 'Email-Adresse Lager');
insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION) 
values ( 203, 'eg.versand.email.scv', 'Kundenbetreuung_Agb@m-net.de', 'Email-Adresse SCV');
insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION) 
values ( 204, 'eg.versand.letzter.export', null, 'Letzter Komplett-Export');
insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION) 
values ( 205, 'eg.versand.letzter.auftragsexport', null, 'Letzter Teil-Export');
insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION) 
values ( 206, 'eg.versand.error.mail', 'GilgAn@m-net.de;HankeMa@m-net.de;GlinkJo@m-net.de', 'Email-Adresse für Fehlermeldungen');
   
   
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (800, 'VERSANDART', 'Post-Versand', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (801, 'VERSANDART', 'Selbstabholung', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (802, 'VERSANDART', 'Vetrieb', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (803, 'VERSANDART', 'Technik', true);
insert into t_reference (ID, TYPE, STR_VALUE, GUI_VISIBLE)
values (804, 'VERSANDART', 'Übernahme', true);

insert into t_service_commands (ID, NAME, CLASS, TYPE, DESCRIPTION) 
values ( 3018, 'report.data.lieferschein', 
'de.augustakom.hurrican.service.reporting.impl.command.GetLieferscheinDatenCommand', 
'REPORT', 'Command-Klasse um Daten fuer einen Lieferschein zu ermitteln.');

alter table t_eg add column LS_TEXT VARCHAR(255) after BESCHREIBUNG;

alter table t_lieferschein add column VERSAND_DATUM DATETIME after PRINTED_AT;

alter table t_lieferschein add column SOFORT_VERSAND BIT after RETOURE_GRUND_ID;

alter table t_lieferschein add column VERSAND_DATUM_EXTERN DATETIME after VERSAND_DATUM;

alter table t_eg_2_auftrag add column BEARBEITER VARCHAR(100) after BEMERKUNG;

insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION) 
values ( 207, 'eg.versand.letzter.import.trackingNo', null, 'Letzter Import der Trackingnummern');

