
Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION,
    VERSION)
 Values
   (5011, 'cps.sdsl.data', 'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetSdslDataCommand',
   'CPS_DATA', 'Sammelt die DSLAM-Daten von SDSL Auftraegen fuer eine CPS-Provisionierung',
    0);

Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO,
    VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5011, 56, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20,
    0);

