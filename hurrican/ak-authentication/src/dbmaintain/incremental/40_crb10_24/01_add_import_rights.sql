-- GUI-Berechtigungen fuer den FTTC KVZ Standort Import
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION, DESCRIPTION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'fttc.kvz.standorte.import.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1, 0, 'FTTC KVZ Standort Import');
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'fttc.kvz.standorte.import.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';

-- GUI-Berechtigungen fuer den UEVT Stifte Import
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION, DESCRIPTION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'import.uevt.stifte.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1, 0, 'UEVT Stifte Import');
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'import.uevt.stifte.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';

-- GUI-Berechtigungen fuer den Baugruppen Import
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION, DESCRIPTION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'import.baugruppen.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1, 0, 'Baugruppen Import');
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'import.baugruppen.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
