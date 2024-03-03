

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.auftrag.LanIpDialog',
    'Button', 'Button, um eine neue IP-Adresse zu vergeben', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 151, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.LanIpDialog';

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.LanIpDialog';
