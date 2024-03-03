update T_SERVICE_CHAIN set NAME='Mnet-OptionalAccount-Check' where ID=23;
update T_SERVICE_CHAIN set NAME='Mnet-OptionalAccount+Buendel-Check' where ID=24;

Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION,
    VERSION)
 Values
   (2019, 'verl.check.opt.accounts', 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAccountsOptionalCommand',
   'VERLAUF_CHECK', 'Command prueft, ob der Auftrag einen aktiven Account besitzt. Account ist optional',
    0);

Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO,
    VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2019, 23, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20,
    0);

Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO,
    VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2019, 24, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20,
    0);
