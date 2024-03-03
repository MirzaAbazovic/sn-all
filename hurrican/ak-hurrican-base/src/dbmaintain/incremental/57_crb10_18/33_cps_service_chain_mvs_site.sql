Insert into T_SERVICE_CHAIN
   (ID, NAME, TYPE, VERSION)
 Values
   (71, 'CPS - MVS Site', 'CPS_DATA', 0);
   
Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
 Values
   (5015, 'cps.mvs.site.data',
   'de.augustakom.hurrican.service.cc.impl.command.cps.mvs.CPSGetMVSSiteDataCommand',
   'CPS_DATA', 'Sammelt die MVS Site Daten',
    0);
    
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5015, 71, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10,
    0);

Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 536, 14000, 71, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 536, 14001, 71, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 536, 14002, 71, 0);