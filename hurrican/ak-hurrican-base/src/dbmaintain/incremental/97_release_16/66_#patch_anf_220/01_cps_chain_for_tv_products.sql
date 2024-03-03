-- M-net Kabel-TV:     PROD_ID 500
-- TV Signallieferung: PROD_ID 521

--
-- T_SERVICE_CHAIN
--
INSERT INTO T_SERVICE_CHAIN
(ID, NAME, TYPE,
 DESCRIPTION, VERSION)
VALUES
  (S_T_SERVICE_CHAIN_0.nextVal, 'CPS - TV', 'CPS_DATA',
   'Chain-Definition, um Daten fuer TV-Produkte zu ermitteln', 0);

--
-- T_CPS_DATA_CHAIN_CONFIG
--

-- create subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 500, 14000, (SELECT
                                                            ID
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE NAME = 'CPS - TV'), 0);

-- modify subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 500, 14001, (SELECT
                                                            ID
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE NAME = 'CPS - TV'), 0);

-- delete subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 500, 14002, 54, 0);

-- lock subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 500, 14006, (SELECT
                                                            ID
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE NAME = 'CPS - TV'), 0);

-- create subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 521, 14000, (SELECT
                                                            ID
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE NAME = 'CPS - TV'), 0);

-- modify subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 521, 14001, (SELECT
                                                            ID
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE NAME = 'CPS - TV'), 0);

-- delete subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 521, 14002, 54, 0);

-- lock subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 521, 14006, (SELECT
                                                            ID
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE NAME = 'CPS - TV'), 0);

--
-- T_SERVICE_COMMAND_MAPPING
--
INSERT INTO T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
VALUES (S_T_SERVICE_COMMAND_MAPPI_0.nextVal,
        (SELECT
           ID
         FROM T_SERVICE_COMMANDS
         WHERE NAME = 'cps.fttx.data'),
        (SELECT
           ID
         FROM T_SERVICE_CHAIN
         WHERE NAME = 'CPS - TV'), 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 0);
