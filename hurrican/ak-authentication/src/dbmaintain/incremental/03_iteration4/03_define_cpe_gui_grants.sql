--
-- Berechtigungen fuer die neuen Endgeraete-Masken (CPE) anlegen
--

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'create.password', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
    'Button', 'Button, um ein zufaelliges Password zu erzeugen', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='create.password'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='create.password'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';


insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'new.endgeraet.ip', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
    'Button', 'Button, um einem EG eine neue IP zuzuordnen', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='new.endgeraet.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='new.endgeraet.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='new.endgeraet.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'del.endgeraet.ip', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
    'Button', 'IP Adresse von EG loeschen', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.endgeraet.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.endgeraet.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.endgeraet.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.auftrag.EndgeraetIpDialog',
    '', '', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EndgeraetIpDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EndgeraetIpDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EndgeraetIpDialog';

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'new.routing', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
    '', '', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='new.routing'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='new.routing'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='new.routing'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'del.routing', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
    '', '', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.routing'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.routing'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.routing'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';


insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'add.acl', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
    '', '', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='add.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='add.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='add.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';


insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'del.acl', 'de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel',
    '', '', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 28, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 26, '1', '1',
  g.ID from GUICOMPONENT g where g.name='del.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel';


