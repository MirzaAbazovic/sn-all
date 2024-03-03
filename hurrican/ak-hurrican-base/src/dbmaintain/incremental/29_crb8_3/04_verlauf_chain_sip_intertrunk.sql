
Insert into T_SERVICE_CHAIN
   (ID, NAME, TYPE, DESCRIPTION, VERSION)
 Values
   (35, 'SIP_InterTrunk_Customer_Check', 'VERLAUF_CHECK', 
   'ServiceChain prueft die Korrektheit von SIP InterTrunk Endkunden Auftraegen', 0);



Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2013, 35, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1, 
    0);
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2009, 35, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2, 
    0);
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2004, 35, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3, 
    0);
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2005, 35, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5, 
    0);
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2006, 35, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 
    0);
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2007, 35, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 7, 
    0);
Insert into T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 Values
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2008, 35, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 8, 
    0);


update T_PRODUKT set VERLAUF_CHAIN_ID=35 where PROD_ID=531;
   