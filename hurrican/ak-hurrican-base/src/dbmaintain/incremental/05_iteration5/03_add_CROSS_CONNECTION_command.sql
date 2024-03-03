--
-- SQL-Script fuer die Definition / Einbindung der CrossConnections
-- fuer die Bauauftraege.
--

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION)
  values (2017, 'verl.check.crossconnections',
  'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAndDefineCrossConnectionsCommand',
  'VERLAUF_CHECK', 'Prueft und setzt ggf. die Default CrossConnections fuer einen DSLAM Port');

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 29, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 19, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 17, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 30, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 23, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 24, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 20, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 31, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 32, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 45, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 16, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 2017, 18, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 20);
