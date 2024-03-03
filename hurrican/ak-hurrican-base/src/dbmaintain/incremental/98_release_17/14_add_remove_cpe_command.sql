Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION)
 Values
   (1015, 'remove.cpe.command',
   'de.augustakom.hurrican.service.cc.impl.command.leistung.RemoveCpeCommand', 'LS_KUENDIGUNG',
   'Command fuehrt notwendige Aktionen aus, wenn die CPE-Leistung von einem Auftrag entfernt wird.');

Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 1015, 560, 'de.augustakom.hurrican.model.cc.TechLeistung', null);

