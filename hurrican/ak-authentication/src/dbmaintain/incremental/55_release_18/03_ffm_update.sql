INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'ba.update.ffm',
  'de.augustakom.hurrican.gui.verlauf.BauauftragFfmPanel', 'BUTTON',
  'FFM - erledigen', 1, 0);
-- Rolle: verlauf.dispo
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 22, '1', '1',
  g.ID from GUICOMPONENT g where g.name='ba.update.ffm';
-- Rolle: verlauf.fieldservice
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 24, '1', '1',
  g.ID from GUICOMPONENT g where g.name='ba.update.ffm';

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'update.ffm',
  'de.augustakom.hurrican.gui.verlauf.VerlaufHistoryDialog', 'BUTTON',
  'FFM - erledigen', 1, 0);
-- Rolle: verlauf.dispo
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 22, '1', '1',
  g.ID from GUICOMPONENT g where g.name='update.ffm';
-- Rolle: verlauf.fieldservice
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 24, '1', '1',
  g.ID from GUICOMPONENT g where g.name='update.ffm';
