--
-- Update-Statements, um die Produkt2Produkt-Konfiguration und die
-- benoetigten ServiceCommands etc. anzulegen.
--

CREATE TABLE T_SERVICE_CHAIN (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , NAME VARCHAR(50) NOT NULL
     , DESCRIPTION VARCHAR(255)
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_SERVICE_COMMANDS (
       ID INTEGER(9) NOT NULL
     , NAME VARCHAR(100) NOT NULL
     , CLASS VARCHAR(255) NOT NULL
     , TYPE CHAR(10) NOT NULL
     , DESCRIPTION TEXT
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_SERVICECHAIN_2_COMMAND (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , CHAIN_ID INTEGER(9) NOT NULL
     , COMMAND_ID INTEGER(9) NOT NULL
     , ORDER_NO TINYINT(3)
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_PROD_2_PROD (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , PROD_SRC INTEGER(9) NOT NULL
     , PROD_DEST INTEGER(9) NOT NULL
     , PHYSIKAEND_TYP INTEGER(9) NOT NULL
     , CHAIN_ID INTEGER(9)
     , DESCRIPTION VARCHAR(255)
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

ALTER TABLE T_SERVICE_COMMANDS
  ADD CONSTRAINT UQ_SERVICE_COMMANDS
      UNIQUE (CLASS);
      
ALTER TABLE T_SERVICECHAIN_2_COMMAND
  ADD CONSTRAINT UQ_SERVICECHAIN2CMD
      UNIQUE (CHAIN_ID, COMMAND_ID);

ALTER TABLE T_SERVICE_CHAIN
  ADD CONSTRAINT UQ_SERVICE_CHAIN
      UNIQUE (NAME);

ALTER TABLE T_PROD_2_PROD
  ADD CONSTRAINT UQ_P2P
      UNIQUE (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP);

ALTER TABLE T_SERVICECHAIN_2_COMMAND
  ADD CONSTRAINT FK_SRVCHAIN_2_COMMAND
      FOREIGN KEY (COMMAND_ID)
      REFERENCES T_SERVICE_COMMANDS (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_SERVICECHAIN_2_COMMAND
  ADD CONSTRAINT FK_SRVCHAIN_2_CHAIN
      FOREIGN KEY (CHAIN_ID)
      REFERENCES T_SERVICE_CHAIN (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_PROD_2_PROD
  ADD CONSTRAINT FK_P2PSRC_2_PROD
      FOREIGN KEY (PROD_SRC)
      REFERENCES T_PRODUKT (PROD_ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_PROD_2_PROD
  ADD CONSTRAINT FK_P2PDEST_2_PROD
      FOREIGN KEY (PROD_DEST)
      REFERENCES T_PRODUKT (PROD_ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_PROD_2_PROD
  ADD CONSTRAINT FK_P2P_PHAENDTYP
      FOREIGN KEY (PHYSIKAEND_TYP)
      REFERENCES T_PHYSIKAENDERUNGSTYP (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_PROD_2_PROD
  ADD CONSTRAINT FK_P2P_2_CHAIN
      FOREIGN KEY (CHAIN_ID)
      REFERENCES T_SERVICE_CHAIN (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

-- Privileges anlegen
GRANT DELETE ON `hurrican`.`T_SERVICECHAIN_2_COMMAND` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`T_SERVICECHAIN_2_COMMAND` TO "hurrican-writer"@"10.2.%";
GRANT DELETE ON `hurrican`.`T_SERVICECHAIN_2_COMMAND` TO "hurrican-writer"@"10.3.%";
GRANT DELETE ON `hurrican`.`T_SERVICECHAIN_2_COMMAND` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`T_SERVICECHAIN_2_COMMAND` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`T_SERVICECHAIN_2_COMMAND` TO "hurrican-writer"@"10.30.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.2.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.3.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.30.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.2.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.3.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.30.%";
FLUSH PRIVILEGES;

--
-- ***********************************************
--
-- Definition der ServiceCommands
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (1, 'change.buendel.no', 'de.augustakom.hurrican.service.cc.impl.command.physik.ChangeBuendelNoCommand', 
	'PHYSIK', 'Schreibt die neue Buendel-No in die Child-Auftraege des alten Auftrags.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (2, 'kuendigung.src.auftrag', 'de.augustakom.hurrican.service.cc.impl.command.physik.KuendigungSrcAuftragCommand', 
	'PHYSIK', 'Setzt den Ursprungs-Auftrag auf gekuendigt und protokolliert dies ueber einen automatisch abgeschlossenen Bauauftrag.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (3, 'move.physik.src2dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.MovePhysikCommand', 
	'PHYSIK', 'Uebertraegt die Physik-Daten des Ursprungs- auf den Ziel-Auftrag. Carrierbestellungen werden ebenfalls übernommen und ggf die Kündigung entfernt.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (4, 'move.tdn.src2dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.MoveTDNCommand', 
	'PHYSIK', 'Uebertraegt die TDN des Ursprungs- auf den Ziel-Auftrag.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (5, 'copy.endgeraet.src2dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.CopyEndgeraetDatenCommand', 
	'PHYSIK', 'Kopiert die Endgeraete-Daten des Ursprungs- auf den Ziel-Auftrag.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (6, 'move.einwahlaccount.src2dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.MoveEinwahlAccountCommand', 
	'PHYSIK', 'Ordnet dem Ziel-Auftrag den Einwahlaccount des Ursprungs-Auftrags zu und sperrt den Einwahlaccount, der dem Ziel-Auftrag zuvor zugeordnet war.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (7, 'change.auftragsart.dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.ChangeAuftragsartCommand', 
	'PHYSIK', 'Aendert die Auftragsart des Ziel-Auftrags abhaengig von der Physikaenderungs-Strategie.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (8, 'ask.and.move.einwahlaccount.src2dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.AskAndMoveEinwahlAccountCommand', 
	'PHYSIK', 'Fragt den Benutzer, ob der Einwahlaccount uebernommen werden soll. Wenn die Frage positiv bestaetigt wird, wird Funktion wie MoveEinwahlaccountCommand ausgefuehrt.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (9, 'ask.and.kuendige.src.auftrag', 'de.augustakom.hurrican.service.cc.impl.command.physik.AskAndKuendigeSrcAuftragCommand', 
	'PHYSIK', 'Fragt den Benutzer, ob der Ursprungs-Auftrag auf <gekuendigt> gesetzt werden soll. Wird die Frage positiv bestaetigt, wird der Ursprungs-Auftrag gekuendigt.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (10, 'log.physikaenderung', 'de.augustakom.hurrican.service.cc.impl.command.physik.LogPhysikAenderungCommand', 
	'PHYSIK', 'Protokolliert die Art der Physik-Aenderung sowie die beteiligten Auftraege mit.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (11, 'copy.ltg.daten.src2dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.CopyLtgDatenCommand', 
	'PHYSIK', 'Kopiert die Leitungs-Daten (z.B. Schnittstelle) des Ursprungs- auf den Ziel-Auftrag.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (12, 'assert.physik.on.es.a.dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.AssertPhysikOnEsADest', 
	'PHYSIK', 'Ueberprueft, ob der Endstelle A des Ziel-Auftrags bereits eine Physik zugeordnet ist. Ist dies nicht der Fall, wird eine Exception erzeugt.');	
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (13, 'assert.physik.on.es.b.dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.AssertPhysikOnEsBDest', 
	'PHYSIK', 'Ueberprueft, ob der Endstelle B des Ziel-Auftrags bereits eine Physik zugeordnet ist. Ist dies nicht der Fall, wird eine Exception erzeugt.');	
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (14, 'assert.no.physik.on.es.a.dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.AssertNoPhysikOnEsADest', 
	'PHYSIK', 'Ueberprueft, ob der Endstelle A des Ziel-Auftrags noch KEINE Physik zugeordnet ist. Ist dies nicht der Fall, wird eine Exception erzeugt.');	
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (15, 'assert.no.physik.on.es.b.dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.AssertNoPhysikOnEsBDest', 
	'PHYSIK', 'Ueberprueft, ob der Endstelle B des Ziel-Auftrags noch KEINE Physik zugeordnet ist. Ist dies nicht der Fall, wird eine Exception erzeugt.');	
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (16, 'move.carrierbestellung', 'de.augustakom.hurrican.service.cc.impl.command.physik.MoveCBCommand', 
	'PHYSIK', 'Uebertraegt die Carrierbestellungen des Ursprungs-Auftrag auf den Ziel-Auftrag.');	
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (17, 'kuendige.carrierbestellung', 'de.augustakom.hurrican.service.cc.impl.command.physik.SetCBKuendigungCommand', 
	'PHYSIK', 'Kuendigt die letzte Carrierbestellung des Ursprungs-Auftrags.');	
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (18, 'eq.out.kreuzen', 'de.augustakom.hurrican.service.cc.impl.command.physik.EqOutKreuzenCommand', 
	'PHYSIK', 'Fuehrt eine Stift-Kreuzung (EQ-Out) zwischen den Rangierungen der Endstellen B des Ursprungs- und Ziel-Auftrags durch.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (19, 'assign.new.dsl.physik.and.eq.kreuzen', 'de.augustakom.hurrican.service.cc.impl.command.physik.AssignNewDSLPhysikCommand', 
	'PHYSIK', 'Entfernt die aktuelle und ordnet dem DSL-Auftrag eine neue Physik zu. Die EQ-Out Stifte der Rangierungen werden gekreuzt! Dies wird allerdings nur dann ausgefuehrt, wenn sich der Phone-Anteil von a/b auf ISDN (od. umgekehrt) handelt!');
--INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
--	values (20, 'validate.cb4talna', 'de.augustakom.hurrican.service.cc.impl.command.physik.ValidateCB4TalNACommand', 
--	'PHYSIK', 'Prueft, ob die Carrierbestellung der Endstelle B des Ziel-Auftrags fuer eine TAL-Nutzungsaenderung gueltig ist.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (21, 'ask.and.copy.vpn.data', 'de.augustakom.hurrican.service.cc.impl.command.physik.AskAndCopyVPNDataCommand', 
	'PHYSIK', 'Kopiert die VPN-Daten des alten Auftrags auf den neuen Auftrag. Allerdings nur, wenn sich die Auftraege im gleichen Kundenkreis (Hauptkunde/Unterkunde) befinden. Ausserdem wird vor der Datenuebernahme noch nachgefragt.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
	values (22, 'copy.telefonbuch.src2dest', 'de.augustakom.hurrican.service.cc.impl.command.physik.CopyTelbuchCommand', 
	'PHYSIK', 'Kopiert die Telefonbuch-Flags vom Ursprungs- auf den Ziel-Auftrag. Allerdings nur, wenn die Kundennummern und die Rufnummern identisch sind!');


-- Definition der ServiceChains
insert into t_service_chain (ID, NAME, DESCRIPTION) 
	values (1, 'Anschlussübernahme - Standard', 'ServiceChain für eine "normale" Anschlussübernahme');
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 15, 1);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 8, 2);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 2, 3);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 3, 4);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 4, 5);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 16, 6);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 11, 7);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 5, 8);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 7, 9);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 21, 10);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (1, 10, 11);
	
insert into t_service_chain (ID, NAME, DESCRIPTION) 
	values (2, 'Anschlussübernahme - SDSL', 'ServiceChain-Definition für eine Anschlussübernahme, wenn SDSL-Aufträge mit enthalten sind.');
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 15, 1);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 8, 2);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 9, 3);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 3, 4);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 4, 5);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 16, 6);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 11, 7);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 5, 8);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 7, 9);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 21, 10);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (2, 10, 11);

insert into t_service_chain (ID, NAME, DESCRIPTION) 
	values (3, 'Bandbreitenänderung', 'ServiceChain, um eine Bandbreitenänderung durchzuführen.');
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 15, 1);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 1, 2);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 3, 3);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 4, 4);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 6, 5);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 5, 6);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 16, 7);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 7, 8);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 2, 9);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 21, 10);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (3, 10, 11);

insert into t_service_chain (ID, NAME, DESCRIPTION) 
	values (4, 'DSL-Kreuzung - Änderung Phone-Anteil', 'ServiceChain-Definition für eine DSL-Kreuzung, bei der lediglich der Phone-Anteil geändert werden soll.');
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (4, 13, 1);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (4, 19, 2);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (4, 16, 3);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (4, 4, 4);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (4, 6, 5);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (4, 10, 6);

insert into t_service_chain (ID, NAME, DESCRIPTION) 
	values (5, 'DSL-Kreuzung (AK-ADSL --> AK-ADSLplus)', 'ServiceChain-Definition für eine DSL-Kreuzung, bei der von z.B. AK-ADSL mit ISDN auf AK-ADSLplus mit a/b gewechselt werden soll.');
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (5, 13, 1);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (5, 18, 2);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (5, 16, 3);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (5, 6, 4);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (5, 4, 5);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (5, 10, 6);

--insert into t_service_chain (ID, NAME, DESCRIPTION) 
--	values (6, 'TAL-Nutzungsänderung - Standard', 'ServiceChain-Definition für eine TAL-Nutzungsänderung. Bei dieser Chain wird der Ursprungs-Auftrag auf "gekündigt" gesetzt.');
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (6, 13, 1);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (6, 20, 2);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (6, 18, 3);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (6, 17, 4);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (6, 2, 5);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (6, 7, 6);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (6, 10, 7);

--insert into t_service_chain (ID, NAME, DESCRIPTION) 
--	values (7, 'TAL-Nutzungsänderung - ADSL<-->SDSL', 'ServiceChain-Definition für eine TAL-Nutzungsänderung, wenn sich das Übertragungsverfahren (z.B. von ADSL auf SDSL) ändert. Der Ursprungs-Auftrag wird hier NICHT auf "gekündigt" gesetzt.');
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (7, 13, 1);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (7, 20, 2);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (7, 18, 3);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (7, 17, 4);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (7, 7, 5);
--insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (7, 10, 6);

insert into t_service_chain (ID, NAME, DESCRIPTION) 
	values (8, 'Wandel analog<-->ISDN', 'ServiceChain-Definition, um einen Wandel von analog auf ISDN (bzw. umgekehrt) durchzuführen.');
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (8, 13, 1);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (8, 18, 2);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (8, 16, 3);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (8, 4, 4);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (8, 10, 5);

insert into t_service_chain (ID, NAME, DESCRIPTION) 
	values (9, 'Bandbreitenänderung - SDSL', 'ServiceChain, um eine Bandbreitenänderung für SDSL-Aufträge durchzuführen.');
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 15, 1);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 1, 2);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 3, 3);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 4, 4);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 5, 5);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 16, 6);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 7, 7);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 2, 8);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 21, 9);
insert into t_servicechain_2_command (CHAIN_ID, COMMAND_ID, ORDER_NO) values (9, 10, 10);

-- Definition der Produkt2Produkt-Zuordnungen
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (1, 1, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (1, 2, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (1, 5, 5003, 8);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (2, 1, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (2, 2, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (2, 5, 5003, 8);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (5, 5, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (5, 1, 5002, 8);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (5, 2, 5003, 8);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 9, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 56, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 57, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 315, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 316, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 317, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 320, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 321, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 56, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 57, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 321, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 315, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 316, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 317, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (9, 320, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (10, 10, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (10, 319, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 11, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 16, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 17, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 18, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 312, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 313, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 16, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 17, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (11, 18, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (12, 12, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 11, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 16, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 17, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 18, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 312, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 313, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 11, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 17, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (16, 18, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 11, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 16, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 17, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 18, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 312, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 313, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 11, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 16, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (17, 18, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 11, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 16, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 17, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 18, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 312, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 313, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 11, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 16, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (18, 17, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (37, 1, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (37, 2, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (38, 1, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (38, 2, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 9, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 56, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 57, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 315, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 316, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 317, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 320, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 321, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 9, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 57, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 321, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 315, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 316, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 317, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (56, 320, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 9, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 56, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 57, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 315, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 316, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 317, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 320, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 321, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 9, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 56, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 321, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 315, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 316, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 317, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (57, 320, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 9, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 56, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 57, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 315, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 316, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 317, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 320, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 321, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 9, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 56, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 57, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 315, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 316, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 317, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (321, 320, 5005, 5);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (58, 1, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (58, 2, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (58, 58, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (58, 59, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (59, 1, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (59, 2, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (59, 58, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (59, 59, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (312, 312, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (312, 313, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (312, 11, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (312, 16, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (312, 17, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (312, 18, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (312, 313, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (313, 312, 5004, 9);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (313, 18, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (313, 17, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (313, 16, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (313, 11, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (313, 313, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (313, 312, 5000, 2);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 9, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 56, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 57, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 315, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 316, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 317, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 320, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 321, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 316, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 317, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 320, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 315, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 316, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 317, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (315, 320, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 9, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 56, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 57, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 315, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 315, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 316, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 315, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 316, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 317, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 317, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 317, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 320, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 320, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 320, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (316, 321, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 9, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 56, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 57, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 315, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 315, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 316, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 315, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 316, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 317, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 316, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 317, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 320, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 320, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 320, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (317, 321, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 9, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 56, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 57, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 315, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 315, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 316, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 315, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 316, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 317, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 316, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 317, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 320, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 317, 5004, 3);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 320, 5005, 4);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (320, 321, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (318, 318, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (319, 319, 5000, 1);
insert into t_prod_2_prod (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) values (319, 10, 5000, 1);
