-- Sicherstellen, dass fuer folgende Produkte das vlan command in den konfigurierten Chains gerufen wird:
-- Maxi Deluxe Fon                    =501 - VoIP_with_ShortTerm_Check
-- Maxi Deluxe Komplett               =503 - VoIP_with_ShortTerm_Check
-- Maxi Deluxe Pur                    =502 - Mnet-DSLonly-Check
-- FTTX Telefon                       =511 - VoIP_Timeslot_Check
-- FTTX DSL                           =512 - VOIP_Account_Timeslot_Check
-- FTTX DSL + Fon                     =513 - VOIP_Account_Timeslot_Check
-- Glasfaser SDSL                     =541 - VoIP_GK_with_ShortTerm_Check
-- Premium Glasfaser-DSL Doppel-Flat  =540 - VoIP_GK_with_ShortTerm_Check
-- Maxi FTTH Wohnheim                 =570 - VOIP With Account and ShortTerm-Check

INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
VALUES (2024, 'verl.check.vlan',
'de.augustakom.hurrican.service.cc.impl.command.verlauf.AssignVlanCommand',
'VERLAUF_CHECK', 'Prueft, und legt ggf. zu einem Auftrag die VLANs an', 0);

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2024,
(select c.ID from T_SERVICE_CHAIN c where c.NAME='VoIP_Timeslot_Check'), 
'de.augustakom.hurrican.model.cc.command.ServiceChain', 
(select max(ORDER_NO)+1 from T_SERVICE_COMMAND_MAPPING where 
REF_ID = (select c.ID from T_SERVICE_CHAIN c where c.NAME='VoIP_Timeslot_Check')  and 
REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'), 0);

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2024,
(select c.ID from T_SERVICE_CHAIN c where c.NAME='VoIP_with_ShortTerm_Check'), 
'de.augustakom.hurrican.model.cc.command.ServiceChain', 
(select max(ORDER_NO)+1 from T_SERVICE_COMMAND_MAPPING where 
REF_ID = (select c.ID from T_SERVICE_CHAIN c where c.NAME='VoIP_with_ShortTerm_Check')  and 
REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'), 0);

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2024,
(select c.ID from T_SERVICE_CHAIN c where c.NAME='Mnet-DSLonly-Check'), 
'de.augustakom.hurrican.model.cc.command.ServiceChain', 
(select max(ORDER_NO)+1 from T_SERVICE_COMMAND_MAPPING where 
REF_ID = (select c.ID from T_SERVICE_CHAIN c where c.NAME='Mnet-DSLonly-Check')  and 
REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'), 0);

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2024,
(select c.ID from T_SERVICE_CHAIN c where c.NAME='VOIP_Account_Timeslot_Check'), 
'de.augustakom.hurrican.model.cc.command.ServiceChain', 
(select max(ORDER_NO)+1 from T_SERVICE_COMMAND_MAPPING where 
REF_ID = (select c.ID from T_SERVICE_CHAIN c where c.NAME='VOIP_Account_Timeslot_Check')  and 
REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'), 0);

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2024,
(select c.ID from T_SERVICE_CHAIN c where c.NAME='VoIP_GK_with_ShortTerm_Check'), 
'de.augustakom.hurrican.model.cc.command.ServiceChain', 
(select max(ORDER_NO)+1 from T_SERVICE_COMMAND_MAPPING where 
REF_ID = (select c.ID from T_SERVICE_CHAIN c where c.NAME='VoIP_GK_with_ShortTerm_Check')  and 
REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'), 0);

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2024,
(select c.ID from T_SERVICE_CHAIN c where c.NAME='VOIP With Account and ShortTerm-Check'), 
'de.augustakom.hurrican.model.cc.command.ServiceChain', 
(select max(ORDER_NO)+1 from T_SERVICE_COMMAND_MAPPING where 
REF_ID = (select c.ID from T_SERVICE_CHAIN c where c.NAME='VOIP With Account and ShortTerm-Check')  and 
REF_CLASS = 'de.augustakom.hurrican.model.cc.command.ServiceChain'), 0);

