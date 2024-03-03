insert into T_SERVICE_CHAIN (ID, NAME, TYPE, VERSION)
 values (90, 'VoIP_Timeslot_Check', 'VERLAUF_CHECK', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2013, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 0, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2010, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2004, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2018, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2008, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 4, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2015, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 0);
update T_PRODUKT set VERLAUF_CHAIN_ID=90 where PROD_ID=511;
 
 
insert into T_SERVICE_CHAIN (ID, NAME, TYPE, VERSION)
 values (91, 'VOIP_Account_Timeslot_Check', 'VERLAUF_CHECK', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2013, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2000, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2010, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2004, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 4, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2018, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2008, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2014, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 7, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2015, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 8, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 9, 0);
update T_PRODUKT set VERLAUF_CHAIN_ID=91 where PROD_ID in (512,513);
