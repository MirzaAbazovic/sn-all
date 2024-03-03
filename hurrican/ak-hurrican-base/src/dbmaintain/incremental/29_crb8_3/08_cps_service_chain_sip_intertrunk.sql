Insert into T_SERVICE_CHAIN
   (ID, NAME, TYPE, VERSION)
 Values
   (65, 'CPS - SIP InterTrunk', 'CPS_DATA', 0);


Insert into T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
 Values
   (5012, 'cps.sip.intertrunk.data',
   'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetSIPInterTrunkDataCommand',
   'CPS_DATA', 'Sammelt die SIP InterTrunk Daten',
    0);


Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5012, 65, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10,
    0);
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5005, 65, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20,
    0);

Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 531, 14000, 65, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 531, 14001, 65, 0);
Insert into T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 Values
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 531, 14006, 65, 0);
