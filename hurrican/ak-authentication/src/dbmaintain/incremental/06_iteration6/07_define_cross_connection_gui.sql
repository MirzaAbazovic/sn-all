

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'cross.connections.zuordnun', 'de.augustakom.hurrican.gui.auftrag.RangierungPanel',
    'Button', 'Button, um den CrossConnection Dialog zu oeffnen', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 23, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cross.connections.zuordnun'
  and g.parent='de.augustakom.hurrican.gui.auftrag.RangierungPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 24, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cross.connections.zuordnun'
  and g.parent='de.augustakom.hurrican.gui.auftrag.RangierungPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 27, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cross.connections.zuordnun'
  and g.parent='de.augustakom.hurrican.gui.auftrag.RangierungPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cross.connections.zuordnun'
  and g.parent='de.augustakom.hurrican.gui.auftrag.RangierungPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 22, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cross.connections.zuordnun'
  and g.parent='de.augustakom.hurrican.gui.auftrag.RangierungPanel';


insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.auftrag.CrossConnectionDialog',
    'Button', 'Button, um CrossConnection zu speichern', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 22, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.CrossConnectionDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 23, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.CrossConnectionDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 24, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.CrossConnectionDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 27, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.CrossConnectionDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.CrossConnectionDialog';




