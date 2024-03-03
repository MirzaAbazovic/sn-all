delete from COMPBEHAVIOR where COMP_ID in
  (select g.ID from GUICOMPONENT g where g.NAME in (
  'command.import.port.action',
  'command.import.leisten.action',
  'command.import.mdu.action',
  'command.import.standort.action',
  'command.tools'
  ));

delete from GUICOMPONENT g where g.NAME in (
  'command.import.port.action',
  'command.import.leisten.action',
  'command.import.mdu.action',
  'command.import.standort.action',
  'command.tools'
  );
