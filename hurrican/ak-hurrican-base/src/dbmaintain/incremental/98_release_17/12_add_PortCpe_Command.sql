Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION)
 Values
   (2029, 'verl.port.cpe.combination.check',
   'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckPortCpeCombinationFttxTelefonCommand', 'VERLAUF_CHECK',
   'Command prueft die Kombination aus verwendetem Port (VDSL/POTS) und gebuchtem Endgeraet; speziell fuer FTTX Telefon');

Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2029, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 12);
