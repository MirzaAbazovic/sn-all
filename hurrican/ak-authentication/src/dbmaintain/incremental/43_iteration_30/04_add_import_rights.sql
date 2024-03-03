-- GUI-Berechtigungen fuer den DTAG KVZ Adressen Import
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION, DESCRIPTION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'import.fttc.kvz.adressen.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1, 0, 'DTAG KVZ Adressen Import');
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'import.fttc.kvz.adressen.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';

-- GUI-Berechtigungen fuer den HVT Adressen Import
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION, DESCRIPTION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'import.hvt.adressen.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1, 0, 'HVT Adressen Import');
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'import.hvt.adressen.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
