Insert into T_SERVICE_CHAIN
   (ID, NAME, TYPE, VERSION)
 Values
   (70, 'CPS - MVS Enterprise', 'CPS_DATA', 0);
   
Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
 Values
   (5014, 'cps.mvs.enterprise.data',
   'de.augustakom.hurrican.service.cc.impl.command.cps.mvs.CPSGetMVSEnterpriseDataCommand',
   'CPS_DATA', 'Sammelt die MVS Enterprise Daten',
    0);
    
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5014, 70, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10,
    0);

Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 535, 14000, 70, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 535, 14001, 70, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 535, 14002, 70, 0);