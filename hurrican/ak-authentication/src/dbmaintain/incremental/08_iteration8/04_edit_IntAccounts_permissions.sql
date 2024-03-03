-- Neue Komponenten, um IntAccount zu aendern
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'edit.account', 'de.augustakom.hurrican.gui.auftrag.internet.InternetPanel',
    'Button', 'Button um einen IntAccount Account zu bearbeiten', 1);

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.auftrag.internet.IntAccountDialog',
    'Button', 'Button manuell IntAccount zu speichern', 1);


-- Spezielle Rolle, um Accounts bearbeiten zu duerfen
Insert into ROLE
   (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN,
    IS_PARENT)
 Values
   (s_role_0.nextval, 'account.admin', 'Rolle um IntAccounts manuell zu bearbeiten', 1, '0', NULL);


-- Zuordnung Komponenten->Rolle
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
  from GUICOMPONENT g, role r
  where g.name='edit.account'
  and g.parent='de.augustakom.hurrican.gui.auftrag.internet.InternetPanel'
  and r.name='account.admin';

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
  from GUICOMPONENT g, role r
  where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.auftrag.internet.IntAccountDialog'
  and r.name='account.admin';
