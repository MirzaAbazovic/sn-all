--
-- GUI Rechte für WAN-IP Dialog auf den Endgeraeten fuer das Auftragsmanagment
--

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.auftrag.WanIpDialog',
    'Button', 'Button, der die zugewiesenen WanIps speichert', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'allocate.new.ip', 'de.augustakom.hurrican.gui.auftrag.WanIpDialog',
    'Button', 'Button, der eine neue WAN-IP in der IP-Datenbank belegt', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1',
  g.ID from GUICOMPONENT g, ROLE r where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.WanIpDialog'
  and r.name = 'auftrag.bearbeiter';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1',
  g.ID from GUICOMPONENT g, ROLE r where g.name='allocate.new.ip'
  and g.parent='de.augustakom.hurrican.gui.auftrag.WanIpDialog'
  and r.name = 'auftrag.bearbeiter';

