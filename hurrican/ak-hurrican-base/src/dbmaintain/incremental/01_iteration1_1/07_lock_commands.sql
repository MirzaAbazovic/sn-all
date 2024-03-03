--
-- SQL-Script traegt die notwendigen Lock-Commands (fuer Kuendigungsverlaeufe)
-- in die DB ein u. modifiziert die notwendigen ServiceChains.
--

insert into T_SERVICE_COMMANDS (ID, NAME, CLASS, DESCRIPTION)
  values (2016, 'verl.check.and.finish.active.lock',
  'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckAndFinishActiveLockCommand',
  'Command prueft, ob aktive Sperre vorhanden ist und setzt diese auf FINISHED');
commit;

-- ServiceChains modifizieren
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (950, 2016, 15, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO)
  values (951, 2016, 13, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 10);
commit;
