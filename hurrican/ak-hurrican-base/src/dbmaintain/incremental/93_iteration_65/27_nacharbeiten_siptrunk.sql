update T_CPS_DATA_CHAIN_CONFIG cc set cc.prod_id = 580 where prod_id = 70;

-- lock-subscriber
insert into T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
  VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextval, 580, 14006, (select id from T_SERVICE_CHAIN where name = 'CPS-SIPTRUNK'), 0);
-- getSoData
insert into T_CPS_DATA_CHAIN_CONFIG (ID, PROD_ID, SO_TYPE_REF_ID, CHAIN_ID, VERSION)
  VALUES (S_T_CPS_DATA_CHAIN_CONFIG_0.nextval, 580, 14009, (select id from T_SERVICE_CHAIN where name = 'CPS-SIPTRUNK'), 0);

update T_PRODUKT set verlauf_chain_id = (SELECT id FROM T_SERVICE_CHAIN WHERE name = 'SIP Trunk Check') where prod_id = 580;

delete from T_PROD_2_TECH_LEISTUNG where prod_id = 70;
delete from T_PRODUKT where prod_id = 70;

update T_PROD_2_TECH_LEISTUNG set PROD_ID = 580 where PROD_ID = 70;

insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
    VALUES (28, 82711, null, 3435, '', 0);
;

insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
    VALUES (28, 82713, null, 3435, '', 0);
;

insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
    VALUES (28, 82732, null, 3435, '', 0);
;
