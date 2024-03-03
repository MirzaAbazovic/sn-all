-- Ansprechpartner- und Addressdialog fuer Auftrags-Bearbeiter

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save.auftrag.ansprechpartner', 'de.augustakom.hurrican.gui.auftrag.AuftragDataFrame',
    'Button', '', 1);

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'add.contact', 'de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel',
    'Button', '', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'remove.contact', 'de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel',
    'Button', '', 1);

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'address.new', 'de.augustakom.hurrican.gui.shared.CCAddressPanel',
    'Button', '', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'address.search', 'de.augustakom.hurrican.gui.shared.CCAddressPanel',
    'Button', '', 1);

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.shared.CCAnsprechpartnerDialog',
    'Button', '', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'cancel', 'de.augustakom.hurrican.gui.shared.CCAnsprechpartnerDialog',
    'Button', '', 1);

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'search', 'de.augustakom.hurrican.gui.shared.FindAddressDialog',
    'Button', '', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.shared.FindAddressDialog',
    'Button', '', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'cancel', 'de.augustakom.hurrican.gui.shared.FindAddressDialog',
    'Button', '', 1);

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save.auftrag.ansprechpartner'
  and g.parent='de.augustakom.hurrican.gui.auftrag.AuftragDataFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save.auftrag.ansprechpartner'
  and g.parent='de.augustakom.hurrican.gui.auftrag.AuftragDataFrame';

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='add.contact'
  and g.parent='de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='remove.contact'
  and g.parent='de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='add.contact'
  and g.parent='de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='remove.contact'
  and g.parent='de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel';

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='address.new'
  and g.parent='de.augustakom.hurrican.gui.shared.CCAddressPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='address.search'
  and g.parent='de.augustakom.hurrican.gui.shared.CCAddressPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='address.new'
  and g.parent='de.augustakom.hurrican.gui.shared.CCAddressPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='address.search'
  and g.parent='de.augustakom.hurrican.gui.shared.CCAddressPanel';

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.shared.CCAnsprechpartnerDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cancel'
  and g.parent='de.augustakom.hurrican.gui.shared.CCAnsprechpartnerDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.shared.CCAnsprechpartnerDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cancel'
  and g.parent='de.augustakom.hurrican.gui.shared.CCAnsprechpartnerDialog';

insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='search'
  and g.parent='de.augustakom.hurrican.gui.shared.FindAddressDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.shared.FindAddressDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cancel'
  and g.parent='de.augustakom.hurrican.gui.shared.FindAddressDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='search'
  and g.parent='de.augustakom.hurrican.gui.shared.FindAddressDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.shared.FindAddressDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 30, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cancel'
  and g.parent='de.augustakom.hurrican.gui.shared.FindAddressDialog';


-- der user zpconnect soll auch Carrierbestellungen erstellen duerfen
insert into USERROLE (ID, USER_ID, ROLE_ID) (select S_USERROLE_0.nextVal, 1440, 9 from dual);

insert into USERROLE (ID, USER_ID, ROLE_ID) (select S_USERROLE_0.nextVal, 1440, 11 from dual);

