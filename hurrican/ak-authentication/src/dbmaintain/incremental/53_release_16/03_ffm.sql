-- MenuItem/Toolbar
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'open.ba.verlauf.ffm.action',
  'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem',
  'FFM MenuItem', 1, 0);

-- Rolle: verlauf.view
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 20, '1', '1',
  g.ID from GUICOMPONENT g where g.name='open.ba.verlauf.ffm.action';



-- Erledigen Button
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'ba.erledigen.ffm',
  'de.augustakom.hurrican.gui.verlauf.BauauftragFfmPanel', 'BUTTON',
  'FFM - erledigen', 1, 0);
-- Rolle: verlauf.dispo
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 22, '1', '1',
  g.ID from GUICOMPONENT g where g.name='ba.erledigen.ffm';
-- Rolle: verlauf.fieldservice
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 24, '1', '1',
  g.ID from GUICOMPONENT g where g.name='ba.erledigen.ffm';
