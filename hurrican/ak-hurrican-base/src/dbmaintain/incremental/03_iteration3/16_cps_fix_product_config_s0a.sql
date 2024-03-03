--
-- Produkt-Konfig fuer S0A korrigieren
--

delete from T_CPS_DATA_CHAIN_CONFIG where PROD_ID in (1,2);

insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (1, 14000, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (1, 14001, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (1, 14006, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (1, 14002, 54);

insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14002, 54);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14006, 50);
