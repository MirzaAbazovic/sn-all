--
-- Weitere Produkt-Konfigurationen fuer den CPS
--

-- LockSubscriber konfigurieren:
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (410, 14006, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (411, 14006, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (420, 14006, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (421, 14006, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (430, 14006, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (431, 14006, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (400, 14006, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (340, 14006, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (336, 14006, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (337, 14006, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (440, 14006, 52);

-- Produkt 2 konfigurieren (AK-phone ISDN S0M)
delete from T_CPS_DATA_CHAIN_CONFIG WHERE PROD_ID=2;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14000, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14001, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14006, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14002, 54);
commit;

-- Produkt 1 konfigurieren (AK-phone ISDN S0A)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='AKphone_ISDN_S0A' where PROD_ID=1;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (1, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (1, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (1, 14002, 54);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (1, 14006, 50);
commit;

-- Produkt 5 konfigurieren (TelAS)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='AKphone_TelAS' where PROD_ID=5;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (5, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (5, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (5, 14002, 54);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (5, 14006, 50);
commit;

-- Produkt 37 konfigurieren (S0A SDSL-S0)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='S0A_SDSL_S0' where PROD_ID=37;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (37, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (37, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (37, 14002, 54);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (37, 14006, 50);
commit;

-- Produkt 38 konfigurieren (S0M SDSL-S0)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='S0M_SDSL_S0' where PROD_ID=38;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (38, 14000, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (38, 14001, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (38, 14002, 54);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (38, 14006, 55);
commit;

-- Produkt 322 konfigurieren (Maxi Analog)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='Maxi_Analog' where PROD_ID=322;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (322, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (322, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (322, 14002, 54);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (322, 14006, 50);
commit;

-- Produkt 323 konfigurieren (Maxi ISDN)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='Maxi_ISDN' where PROD_ID=323;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (323, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (323, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (323, 14002, 54);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (323, 14006, 50);
commit;



