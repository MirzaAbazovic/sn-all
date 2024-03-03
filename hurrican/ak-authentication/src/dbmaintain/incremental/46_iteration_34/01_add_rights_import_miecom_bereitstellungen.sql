-- GUI-Berechtigungen fuer Miecom Bereitstellungen Import
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION, DESCRIPTION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'import.miecom.bereitstellungen.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1, 0, 'Miecom Bereitstellungen Import');
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 596, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'import.miecom.bereitstellungen.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
