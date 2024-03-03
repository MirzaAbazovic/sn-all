-- Verlauf Chains die updaten werden sollen:  
--    VoIP_GK_with_ShortTerm_Check ID = 125
--    VOIP With Account and ShortTerm-Check ID = 45
--    VOIP_Account_Timeslot_Check ID = 91
--    VoIP_Timeslot_Check ID = 90
--    Mnet-DSLPhone-Check ID = 16
--    Mnet-OptionalAccount+Buendel-Check ID = 24

  
-- Remove alle Command Mappings for Command 2025

DELETE FROM  T_SERVICE_COMMAND_MAPPING
  WHERE COMMAND_ID = 2025;