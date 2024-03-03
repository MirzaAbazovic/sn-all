INSERT INTO T_SERVICE_CHAIN (ID, NAME, TYPE,
                             DESCRIPTION, VERSION)
    VALUES (S_T_SERVICE_CHAIN_0.nextval, 'CPS-SIPTRUNK', 'CPS_DATA',
            'ermittelt CPS-relevante Daten für das Produkt SIP Trunk', 0)
;

-- create subscriber
insert into T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
  VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextval, 70, 14000, (select id from T_SERVICE_CHAIN where name = 'CPS-SIPTRUNK'), 0);
-- modify subscriber
insert into T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
  VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextval, 70, 14001, (select id from T_SERVICE_CHAIN where name = 'CPS-SIPTRUNK'), 0);
-- cancel-subscriber -> enthaelt command um ggf. abzugebende rufnummern zu portieren
insert into T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
  VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextval, 70, 14002, 54, 0);
