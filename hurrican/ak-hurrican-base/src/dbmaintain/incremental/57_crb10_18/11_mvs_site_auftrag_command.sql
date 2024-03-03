INSERT INTO T_SERVICE_COMMANDS
   (ID, NAME, CLASS, TYPE, DESCRIPTION, VERSION)
 VALUES
   (2023, 'verl.check.mvs.site',
   'de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckMVSSiteCommand',
   'VERLAUF_CHECK', 'Prueft, ob MVS Site Auftragdaten gesetzt sind',
   0);

insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values 
  (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, 
  (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.mvs.site'), 
  (select c.ID from T_SERVICE_CHAIN c where c.NAME='MVS-Site_Check'), 
  'de.augustakom.hurrican.model.cc.command.ServiceChain', 
  9, 
  0);
  
  
  