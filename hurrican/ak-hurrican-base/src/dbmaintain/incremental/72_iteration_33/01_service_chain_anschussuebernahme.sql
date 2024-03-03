-- aktuelle chain umbenennen
update T_SERVICE_CHAIN set NAME = 'Anschluss�bernahme - Standard GK',
    DESCRIPTION = 'ServiceChain f�r eine "normale" Anschluss�bernahme mit Bandbreitencheck (Gesch�ftskunden)' where ID = 1;

-- chain kopieren
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION, VERSION) 
    VALUES (S_T_SERVICE_CHAIN_0.NEXTVAL, 'Anschluss�bernahme - Standard PK', 'PHYSIK', 'ServiceChain f�r eine "normale" Anschluss�bernahme von ADSL1 Ports f�r Surf&Fon Produkte (Privatkunden)', 0);

-- neues command eintragen    
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
    VALUES (31, 'assign.dslamprofile.4bandwidth',
      'de.augustakom.hurrican.service.cc.impl.command.physik.AssignDslamProfile4BandwidthCommand',
      'PHYSIK', 'Weist dem neuen Auftrag ein Standard 6000-er DSLAMProfil zu, wenn die Downstream Bandbreite des neuen Auftrags mit der Physik des alten Auftrages nicht realisiert werden kann.', 0);
    
-- chain mapping f�r neue chain (Kopie von chain mit ID = 1)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    15,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',1,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    24,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',2,0);
-- ersetze 'check.downstream.bandwidth' mit 'assign.dslamprofile.4bandwidth'
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    31,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',3,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    8,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',4,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    2,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',5,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    3,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',6,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    4,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',7,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    16,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',8,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    11,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',9,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    5,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',10,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    22,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',11,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    7,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',12,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    21,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',13,0);
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID,COMMAND_ID,REF_ID,REF_CLASS,ORDER_NO,VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    10,(SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK'),'de.augustakom.hurrican.model.cc.command.ServiceChain',14,0);

-- neue Chain in Prod2Prod eintragen
update T_PROD_2_PROD pp set pp.CHAIN_ID = (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschluss�bernahme - Standard PK') where pp.CHAIN_ID = 1 AND pp.PROD_DEST in (420, 421, 440);