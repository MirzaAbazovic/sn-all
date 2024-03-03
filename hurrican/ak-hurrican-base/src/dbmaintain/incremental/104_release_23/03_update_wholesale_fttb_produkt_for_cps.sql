-- CPS-Konfiguration fuer Wholesale-Produkt
UPDATE T_PRODUKT SET
  CPS_PROVISIONING='1',
  CPS_PROD_NAME='Wholesale_FTTBH',
  CPS_AUTO_CREATION='1',
  CPS_ACCOUNT_TYPE=NULL,
  CPS_DSL_PRODUCT=NULL
  WHERE PROD_ID = 600;


-- Neue CPS Service Chain fuer Wholesale Produkte anlegen
INSERT INTO T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
  VALUES (S_T_SERVICE_CHAIN_0.nextval, 'CPS - Wholesale', 'CPS_DATA', 'Chain-Definition, um Daten fuer Wholesale Produkte zu ermitteln.');

-- Wholesale Produkt mit CPS Chain und CPS_SERVICE_ORDER_TYPE (14000 = createSubscriber, 14001 = modifySubscriber, 14002 = deleteSubscriber) verknuepfen
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 600, 14000, (select id from T_SERVICE_CHAIN where name = 'CPS - Wholesale'));
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 600, 14001, (select id from T_SERVICE_CHAIN where name = 'CPS - Wholesale'));
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 600, 14002, (select id from T_SERVICE_CHAIN where name = 'CPS - Wholesale'));

-- Command Mapping (COMMAND_ID = ID aus T_SERVICE_COMMANDS; REF_ID = ID aus T_SERVICE_CHAIN, ORDER_NO = Sortierung)
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5006, (select id from T_SERVICE_CHAIN where name = 'CPS - Wholesale'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 0);
