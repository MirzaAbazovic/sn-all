-- CPS Chain fuer Glasfaser-Produkte aendern

-- DSL-Command in Chain mit aufnehmen

Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO,
    VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5002, 53, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 70,
    0);
