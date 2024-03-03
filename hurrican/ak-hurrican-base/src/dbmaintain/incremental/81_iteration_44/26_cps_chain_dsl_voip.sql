-- CHAIN
INSERT INTO T_SERVICE_CHAIN (ID, NAME, TYPE, VERSION, 
 DESCRIPTION)
 VALUES (59, 'CPS - DSL+VOIP am HVT', 'CPS_DATA', 0, 
 'Chain-Definition, um Daten fuer das Produkt dsl+voip zu sammeln. Enthaelt im Gegensatz zu ID 52 ein commannd um Device-Daten zu sammeln.');
 
-- Commands 4 Chain
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5001, 59, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5003, 59, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5004, 59, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5005, 59, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 40, 
    0);
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, 
    VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5002, 59, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 50, 
    0);

-- map CPS-Chain 2 Produkt
INSERT INTO T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 VALUES
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 480, 14000, 59, 0);
INSERT INTO T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 VALUES
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 480, 14001, 59, 0);
INSERT INTO T_CPS_DATA_CHAIN_CONFIG
   (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
 VALUES
   (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 480, 14002, 54, 0);
