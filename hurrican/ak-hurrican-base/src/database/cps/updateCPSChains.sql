--
-- SQL-Script definiert die ServiceChain-Commands sowie die Chains
-- und die Konfiguration mit Produkt/SO-Type.
--

-- Command-Definition
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5000, 'cps.ewsd.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetEWSDDataCommand', 
  'CPS_DATA', 'Sammelt die Rufnummern-Daten (EWSD) fuer eine CPS-Provisionierung');
  
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5001, 'cps.voip.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetVoIPDataCommand', 
  'CPS_DATA', 'Sammelt die Rufnummern-Daten (VoIP) fuer eine CPS-Provisionierung');
  
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5002, 'cps.dsl.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDSLDataCommand', 
  'CPS_DATA', 'Sammelt die DSL-Daten fuer eine CPS-Provisionierung');

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5003, 'cps.radius.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusDataCommand', 
  'CPS_DATA', 'Sammelt die Radius-Daten fuer eine CPS-Provisionierung');

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5004, 'cps.device.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDeviceDataCommand', 
  'CPS_DATA', 'Sammelt die Endgeraete-Daten fuer eine CPS-Provisionierung');
commit;

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5005, 'cps.dn.portation.going.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDNPortationGoingDataCommand', 
  'CPS_DATA', 'Sammelt die Daten von abgehenden Portierungen');
commit;

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5006, 'cps.fttx.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetFTTXDataCommand', 
  'CPS_DATA', 'Sammelt die notwendigen FTTX Daten');
commit;

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5007, 'cps.dsl.optional.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDSLOptionalDataCommand', 
  'CPS_DATA', 'Sammelt die DSL-Daten fuer eine CPS-Provisionierung; Ermittlung ist optional!');
commit;

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (5008, 'cps.radius.optional.data', 
  'de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetRadiusOptionalDataCommand', 
  'CPS_DATA', 'Sammelt die Radius-Daten fuer eine CPS-Provisionierung; Ermittlung ist optional!');
commit;


-- Chain-Definition
-- Chain fuer reine Telephonie Produkte (ueber EWSD)
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
  values (50, 'CPS - Voice (EWSD)', 'CPS_DATA', 
  'Chain-Definition, um Daten fuer reine Telephone (EWSD) Produkte zu ermitteln.');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1000, 5000, 50, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1001, 5008, 50, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1002, 5005, 50, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30);  
  
-- Chain fuer kombinierte DSL/Phone Produkte (ueber EWSD)
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
  values (51, 'CPS - DSL+Voice (EWSD)', 'CPS_DATA', 
  'Chain-Definition, um Daten fuer kombinierte DSL/Phone (EWSD) Produkte zu ermitteln.');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1010, 5000, 51, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1011, 5002, 51, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1012, 5003, 51, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1013, 5005, 51, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 40); 
  
-- Chain fuer kombinierte DSL/Phone Produkte (ueber VoIP) (Phone optional)
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
  values (52, 'CPS - DSL+Voice (VoIP)', 'CPS_DATA', 
  'Chain-Definition, um Daten fuer kombinierte DSL/Phone (VoIP) Produkte zu ermitteln.');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1020, 5001, 52, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1021, 5002, 52, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1022, 5003, 52, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1023, 5005, 52, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 40); 

-- Chain fuer kombinierte Deluxe Produkte
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
  values (53, 'CPS - FttX', 'CPS_DATA', 
  'Chain-Definition, um Daten fuer FttX-Produkte zu ermitteln.');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1030, 5001, 53, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1031, 5003, 53, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1032, 5004, 53, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 40);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1033, 5005, 53, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 50); 
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1034, 5006, 53, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 60); 
commit;

-- Chain-Definition fuer Kuendigungen (enthaelt keine Commands!)
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
  values (54, 'CPS - Cancel Subscriber', 'CPS_DATA', 
  'Chain-Definition, um einen Auftrag zu kuendigen - keine Daten-Commands!.');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1035, 5005, 54, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10); 
commit;


-- Chain fuer reine TK Produkte (ueber EWSD)
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION)
  values (55, 'CPS - Voice TK (EWSD)', 'CPS_DATA', 
  'Chain-Definition, um Daten fuer TK-Anlagen (EWSD) Produkte zu ermitteln.');
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1036, 5000, 55, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1037, 5008, 55, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1038, 5007, 55, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (1039, 5005, 55, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 40);  
commit;


-- Sequence hoch setzen
drop sequence S_T_SERVICE_COMMAND_MAPPI_0;
create sequence S_T_SERVICE_COMMAND_MAPPI_0 start with 1100;
grant select on S_T_SERVICE_COMMAND_MAPPI_0 to public;


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

