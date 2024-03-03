-- CPS-Konfiguration fuer IPSec C2S

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5010, 'cps.radius.ipsec.c2s.data',
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusIPSecC2SDataCommand',
  'CPS_DATA', 'Sammelt die Radius-Daten fuer eine CPS-Provisionierung; Ermittlung fuer IPSec C2S!');

-- Chain fuer reine SDSL Produkte
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
  values (57, 'CPS - IPSec C2S', 'CPS_DATA',
  'Chain-Definition, um Daten fuer IPSec C2S Produkte zu ermitteln.');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5010,
  57, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10);

update T_PRODUKT set CPS_PROVISIONING='1', CPS_PROD_NAME='IPSEC_C2S', CPS_AUTO_CREATION='0',
  CPS_ACCOUNT_TYPE='IPSEC_C2S'
  where PROD_ID=442;

insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (442, 14000, 57);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (442, 14001, 57);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (442, 14002, 54);
insert into T_CPS_DATA_CHAIN_CONFIG (PROD_ID, SO_TYPE_REF_ID, CHAIN_ID)
  values (442, 14006, 57);
