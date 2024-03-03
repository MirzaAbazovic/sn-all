-- GUI-Berechtigungen fuer den AuftragTv2GeoId Import
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION, DESCRIPTION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'import.auftragTv2GeoId.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1, 0, 'Koax Mitversorgung Import');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
     VALUES (S_COMPBEHAVIOR_0.nextVal, 
        (SELECT ID FROM ROLE WHERE NAME = 'wowi.import'), 
        '1', '1', 
        (SELECT ID FROM GUICOMPONENT WHERE name = 'import.auftragTv2GeoId.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame'));
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'import.auftragTv2GeoId.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
