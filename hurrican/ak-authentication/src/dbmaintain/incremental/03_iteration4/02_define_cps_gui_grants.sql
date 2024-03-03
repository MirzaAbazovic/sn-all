--
-- Script definiert die Berechtigungen fuer die neuen GUI-Elemente
--   - CPS
--   - Endgeraete
--

-- neue Rolle fuer CPS-Bearbeiter anlegen
insert into ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, IS_PARENT)
  values (150, 'cps.worker', 'Rolle fuer Benutzer, die mit dem CPS arbeiten', 1, '0', '0');

-- CPS Rolle den Usern zuordnen
insert into USERROLE (ID, ROLE_ID, USER_ID)
  select S_USERROLE_0.nextVal, 150, u.ID from USERS u where DEP_ID in (1,2,3) and ACTIVE='1';

-- CPS Rollenrechte vergeben
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'cps.tools', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'Menu', 'Menu fuer die CPS-Tools', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'cps.transaction.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'MenuItem', 'MenuItem zur Anzeige der offenen CPS-Tx', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'open.cps.tx.creation.dlg.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'MenuItem', 'MenuItem, um eine neue CPS-Tx anzulegen', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'cps.history.anzeigen', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'MenuItem', 'MenuItem, um die CPS-Tx History anzuzeigen', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'save', 'de.augustakom.hurrican.gui.cps.CPSTxCreationDialog',
    'Button', 'Button, um eine neue CPS-Tx auszuloesen', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'show.cps.tx.history', 'de.augustakom.hurrican.gui.verlauf.BauauftragIPSPanel',
    'Button', 'CPS-Tx History fuer ST-Online', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'create.cps.tx', 'de.augustakom.hurrican.gui.verlauf.BauauftragIPSPanel',
    'Button', 'CPS-Tx erstellen fuer ST-Online', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'show.cps.tx.history', 'de.augustakom.hurrican.gui.verlauf.BauauftragEWSDPanel',
    'Button', 'CPS-Tx History fuer ST-Voice', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'create.cps.tx', 'de.augustakom.hurrican.gui.verlauf.BauauftragEWSDPanel',
    'Button', 'CPS-Tx erstellen fuer ST-Voice', 1);
insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'show.cps.tx.history', 'de.augustakom.hurrican.gui.verlauf.BauauftragSDHPanel',
    'Button', 'CPS-Tx History fuer ST-Connect', 1);

-- GUI-Components der CPS-Rolle zuordnen
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cps.tools'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cps.transaction.action'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='open.cps.tx.creation.dlg.action'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='cps.history.anzeigen'
  and g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='save'
  and g.parent='de.augustakom.hurrican.gui.cps.CPSTxCreationDialog';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='show.cps.tx.history'
  and g.parent='de.augustakom.hurrican.gui.verlauf.BauauftragIPSPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='create.cps.tx'
  and g.parent='de.augustakom.hurrican.gui.verlauf.BauauftragIPSPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='show.cps.tx.history'
  and g.parent='de.augustakom.hurrican.gui.verlauf.BauauftragEWSDPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='create.cps.tx'
  and g.parent='de.augustakom.hurrican.gui.verlauf.BauauftragEWSDPanel';
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 150, '1', '1',
  g.ID from GUICOMPONENT g where g.name='show.cps.tx.history'
  and g.parent='de.augustakom.hurrican.gui.verlauf.BauauftragSDHPanel';








