-- TV Signallieferung: PROD_ID 521

--
-- T_CPS_DATA_CHAIN_CONFIG
--

--  create subscriber
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
-- T_PRODUKT
--
UPDATE T_PRODUKT
SET CPS_PROVISIONING = 1, CPS_PROD_NAME = 'TV_Signallieferung', CPS_AUTO_CREATION = 1
WHERE prod_id = 521;
