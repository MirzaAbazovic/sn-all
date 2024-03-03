

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'allocate.new.ip', 'de.augustakom.hurrican.gui.auftrag.internet.InternetPanel',
    'Button', 'Button, um eine neue IP-Adresse zu vergeben', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 151, '1', '1',
  g.ID from GUICOMPONENT g where g.name='allocate.new.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.internet.InternetPanel';


