--
-- SQL-Script fuehrt die Produkt-Konfiguration
-- fuer den CPS durch.
--

-- Produkt/SO-Type Zuordnung
-- Produkt 420 konfigurieren (Maxi DSL + ISDN)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='MaxiDSL+ISDN' where PROD_ID=420;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (420, 14000, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (420, 14001, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (420, 14002, 54);
-- Produkt 421 konfigurieren (Maxi DSL + analog)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='MaxiDSL+analog' where PROD_ID=421;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (421, 14000, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (421, 14001, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (421, 14002, 54);

-- Produkt 503 konfigurieren (Maxi Deluxe Komplett)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='MaxiDeluxeKomplett' where PROD_ID=503;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (503, 14000, 53);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (503, 14001, 53);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (503, 14002, 54);

commit;

-- Produkt 513 konfigurieren (Maxi Komplett Deluxe - NEU)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='MaxiKomplettDeluxe' where PROD_ID=513;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (513, 14000, 53);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (513, 14001, 53);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (513, 14002, 54);
commit;

-- Produkt 430 konfigurieren (Premium DSL + ISDN)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='PremiumDSL+ISDN' where PROD_ID=430;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (430, 14000, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (430, 14001, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (430, 14002, 54);
-- Produkt 431 konfigurieren (Premium DSL + analog)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='PremiumDSL+analog' where PROD_ID=431;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (431, 14000, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (431, 14001, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (431, 14002, 54);
commit;

-- Produkt 400 konfigurieren (AK-ADSL_ISDN)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='AK-ADSL_ISDN' where PROD_ID=400;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (400, 14000, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (400, 14001, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (400, 14002, 54);

-- Produkt 410 konfigurieren (AK-DSLplus + ISDN)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='AKDSLplus_ISDN' where PROD_ID=410;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (410, 14000, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (410, 14001, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (410, 14002, 54);
-- Produkt 411 konfigurieren (AK-DSLplus + analog)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='AKDSLplus_analog' where PROD_ID=411;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (411, 14000, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (411, 14001, 51);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (411, 14002, 54);

-- Produkt 2 konfigurieren (AK-phone ISDN S0M)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='AKphoneISDNS0M' where PROD_ID=2;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (2, 14002, 54);
commit;

-- Produkt 340 konfigurieren (ANALOG)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='ANALOG' where PROD_ID=340;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (340, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (340, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (340, 14002, 54);
commit;

-- Produkt 336 konfigurieren (PremiumCall ISDN MSN)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='PremiumCallISDNMSN' where PROD_ID=336;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (336, 14000, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (336, 14001, 50);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (336, 14002, 54);
commit;

-- Produkt 337 konfigurieren (PremiumCall ISDN TK)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='PremiumCallISDN_TK' where PROD_ID=337;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (337, 14000, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (337, 14001, 55);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (337, 14002, 54);
commit;

-- Produkt 440 konfigurieren (MaxiPur)
update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='MaxiPUR' where PROD_ID=440;
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (440, 14000, 52);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (440, 14001, 52);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (440, 14002, 54);
commit;

