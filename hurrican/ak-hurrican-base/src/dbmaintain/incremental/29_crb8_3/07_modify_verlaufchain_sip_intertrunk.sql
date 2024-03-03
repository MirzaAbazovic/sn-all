
Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
 Values
   (2020, 'verl.check.asb', 'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAsbDefinitionCommand',
   'VERLAUF_CHECK', 'Command prueft, ob Endstelle einen techn. Std. mit ASB Kennung besitzt',
    0);

update T_SERVICE_COMMAND_MAPPING set COMMAND_ID=2020 where COMMAND_ID=2007 and REF_ID=35 
    and REF_CLASS='de.augustakom.hurrican.model.cc.command.ServiceChain';
