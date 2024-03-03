-- Command-Definition
insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (2025, 'check.bestellung.aktiviert', 
  'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckKomsaBestellungCommand', 
  'VERLAUF_CHECK', 'Ueberpruft ob alle Komsa Bestellungen aktiviert sind.');
  
-- Verlauf Chains die updaten werden sollen:  
--    VoIP_GK_with_ShortTerm_Check ID = 125
--    VOIP With Account and ShortTerm-Check ID = 45
--    VOIP_Account_Timeslot_Check ID = 91
--    VoIP_Timeslot_Check ID = 90
--    Mnet-DSLPhone-Check ID = 16
--    Mnet-OptionalAccount+Buendel-Check ID = 24

  
-- Command Mappings

--    VoIP_GK_with_ShortTerm_Check ID = 125
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2025, 125, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 0);

--    VOIP With Account and ShortTerm-Check ID = 45
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2025, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10, 0);
   
--    VOIP_Account_Timeslot_Check ID = 91
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2025, 91, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 11, 0);
   
--    VoIP_Timeslot_Check ID = 90
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2025, 90, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 9, 0);
   
--    Mnet-DSLPhone-Check ID = 16
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2025, 16, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 12, 0);
   
--    Mnet-OptionalAccount+Buendel-Check ID = 24
INSERT INTO T_SERVICE_COMMAND_MAPPING
   (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
 VALUES
   (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2025, 24, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 22, 0);   
   
   