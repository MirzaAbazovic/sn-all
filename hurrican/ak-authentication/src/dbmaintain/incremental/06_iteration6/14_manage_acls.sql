--
-- GUI Rechte für die ACL-Verwaltung setzen: hinzufügen, ändern, editieren. Das darf nur das VPN-Team
--

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.auftrag.EndgeraetAclEditDialog',
    'Button', 'Button, der die geaenderte ACL speichert', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'new.acl', 'de.augustakom.hurrican.gui.auftrag.EndgeraetAclDialog',
    'Button', 'Button, der eine neue ACL anlegt', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'del.acl', 'de.augustakom.hurrican.gui.auftrag.EndgeraetAclDialog',
    'Button', 'Button, der eine ACLs loescht', 1);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1',
  g.ID from GUICOMPONENT g, ROLE r where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EndgeraetAclEditDialog'
  and r.name = 'auftrag.bearbeiter.vpn';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1',
  g.ID from GUICOMPONENT g, ROLE r where g.name='new.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EndgeraetAclDialog'
  and r.name = 'auftrag.bearbeiter.vpn';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1',
  g.ID from GUICOMPONENT g, ROLE r where g.name='del.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EndgeraetAclDialog'
  and r.name = 'auftrag.bearbeiter.vpn';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1',
  g.ID from GUICOMPONENT g, ROLE r where g.name='add.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel'
  and r.name = 'auftrag.bearbeiter.vpn';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1',
  g.ID from GUICOMPONENT g, ROLE r where g.name='del.acl'
  and g.parent='de.augustakom.hurrican.gui.auftrag.EGConfigurationPanel'
  and r.name = 'auftrag.bearbeiter.vpn';




