INSERT INTO T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
 VALUES
   (1013, 'add.ipv4',
   'de.augustakom.hurrican.service.cc.impl.command.leistung.IpV4ReservierenCommand',
   'LS_ZUGANG', 'Ordnet dem Auftrag falls noetig eine IPV4-Adresse zu',
   0);

INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1013, 4, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);
    
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1013, 5, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);
    
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
    VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1013, 6, 'de.augustakom.hurrican.model.cc.TechLeistung', NULL, 0);