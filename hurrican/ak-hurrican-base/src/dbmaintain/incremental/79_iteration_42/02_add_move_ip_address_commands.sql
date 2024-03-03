-- MoveIpAddressesCommand eintragen
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
    VALUES (31, 'move.ip.addresses.src2dest',
      'de.augustakom.hurrican.service.cc.impl.command.ipaddress.MoveIpAddressesCommand',
      'PHYSIK', 'Ordnet dem Ziel-Auftrag die IP Adressen des Ursprungs-Auftrags zu, und gibt alle IP Adressen frei, die dem Ziel-Auftrag zuvor zugeordnet waren.', 0);

-- AskAndMoveIpAddressesCommand eintragen
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
    VALUES (32, 'ask.and.move.ip.addresses.src2dest',
      'de.augustakom.hurrican.service.cc.impl.command.ipaddress.AskAndMoveIpAddressesCommand',
      'PHYSIK', 'Fraegt den User, ob dem Ziel-Auftrag die IP Adressen des Ursprungs-Auftrags zugeordnet werden sollen, und gibt alle IP Adressen frei, die dem Ziel-Auftrag zuvor zugeordnet waren.', 0);

-- Service Chain Mapping fuer die Ask Variante
-- Anschlussübernahme - Standard GK -> ID = 1
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    32, (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschlussübernahme - Standard GK'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 15, 0);
-- Anschlussübernahme - SDSL -> ID = 2
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    32, (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschlussübernahme - SDSL'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 13,0);
-- Anschlussüb. - DSL+phone auf Kombi -> ID = 10
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    32, (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschlussüb. - DSL+phone auf Kombi'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 17, 0);
-- Anschlussübernahme - Standard PK -> ID = 150
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    32, (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Anschlussübernahme - Standard PK'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 15, 0);

-- Service Chain Mapping fuer die Variante ohne Rueckfrage
-- Bandbreitenänderung -> ID = 3
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    31, (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Bandbreitenänderung'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 14, 0);
-- DSL-Kreuzung - Änderung Phone-Anteil -> ID = 4
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    31, (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='DSL-Kreuzung - Änderung Phone-Anteil'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 9, 0);
-- DSL-Kreuzung - Produktwechsel -> ID = 5
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    31, (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='DSL-Kreuzung - Produktwechsel'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 8, 0);
-- Bandbreitenänderung - SDSL -> ID = 9
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION) VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
    31, (SELECT c.ID FROM T_SERVICE_CHAIN c WHERE c.NAME='Bandbreitenänderung - SDSL'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 13, 0);
