-- WBCI Toolbar Button und Menu-Item
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.vorabstimmung.action'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 22, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.vorabstimmung.action'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 25, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.vorabstimmung.action'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.vorabstimmung.action'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.vorabstimmung.action'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';

-- Parent Menu 'WBCI'
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.tools'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 22, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.tools'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 25, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.tools'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.tools'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='wbci.tools'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';

