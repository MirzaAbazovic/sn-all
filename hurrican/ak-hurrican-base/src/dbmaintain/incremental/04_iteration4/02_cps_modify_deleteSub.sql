--
-- ServiceChain fuer DeleteSubscriber aendern
--   --> Portation Block aufnehmen
--

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5005, 54, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1);
