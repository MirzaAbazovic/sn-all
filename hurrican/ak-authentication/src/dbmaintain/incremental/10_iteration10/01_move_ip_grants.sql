
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'move.ip', 'de.augustakom.hurrican.gui.auftrag.internet.IPPanel',
    'MenuItem', 'Popup-MenuItem, um eine IP-Adresse umzuziehen', 1);

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='move.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.internet.IPPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 316, '1', '1',
  g.ID from GUICOMPONENT g where g.name='move.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.internet.IPPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 397, '1', '1',
  g.ID from GUICOMPONENT g where g.name='move.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.internet.IPPanel';

