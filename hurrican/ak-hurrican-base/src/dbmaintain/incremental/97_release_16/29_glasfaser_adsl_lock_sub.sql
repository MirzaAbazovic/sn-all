-- lock subscriber
INSERT INTO T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextVal, 542, 14006, (SELECT
                                                            id
                                                          FROM T_SERVICE_CHAIN
                                                          WHERE name = 'CPS - FttX ADSL'), 0);
