INSERT INTO T_SERVICE_COMMANDS
(ID, NAME, CLASS, TYPE, DESCRIPTION,
 VERSION)
VALUES
  (5016, 'cps.peeringpartner.data',
   'de.augustakom.hurrican.service.cc.impl.command.cps.CpsGetPeeringPartnerDataCommand', 'CPS_DATA',
   'Sammelt die SBC-IPs der konf. Peering-Partner',
   0);

-- Glasfaser SDSL
INSERT INTO T_SERVICE_COMMAND_MAPPING
(ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO,
 VERSION)
VALUES
  (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5016, 58, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 7,
   0);

-- Glasfaser ADSL
INSERT INTO T_SERVICE_COMMAND_MAPPING
(ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO,
 VERSION)
VALUES
  (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5016, 210, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 60,
   0);

-- SDSL
INSERT INTO T_SERVICE_COMMAND_MAPPING
(ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO,
 VERSION)
VALUES
  (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 5016, 56, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 30,
   0);
