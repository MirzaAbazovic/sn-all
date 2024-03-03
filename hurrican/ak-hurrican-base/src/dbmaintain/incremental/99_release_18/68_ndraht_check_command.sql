Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION,
    VERSION)
 Values
   (2030, 'verl.sdsl.ndraht.fttc.check', 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckSdslNDrahtFttcCommand',
   'VERLAUF_CHECK', 'Command prueft bei FTTC und Bandbreite 50/50, ob n-Draht Option mit Rangierung vorhanden ist.',
    0);

Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO,
    VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2030,
   (select c.ID from T_SERVICE_CHAIN c where c.NAME='VoIP_GK_with_ShortTerm_Check'),
   'de.augustakom.hurrican.model.cc.command.ServiceChain', 15,
    0);
