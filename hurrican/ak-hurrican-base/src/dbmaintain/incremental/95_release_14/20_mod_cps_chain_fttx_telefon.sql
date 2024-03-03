-- T_SERVICE_CHAIN: ID = 64, NAME = CPS - FTTX (Telefon)
-- T_SERVICE_COMMANDS:
--   ID = 5004, NAME = cps.device.data (neu hinzufügen)
--   ID = 5003, NAME = cps.radius.data (ist bereits in der Chain enthalten)

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5004, 64, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5);