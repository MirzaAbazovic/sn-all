-- 'Administration/Geo IDs...' Menü Eintrag für Rolle 'admin.strassen' freigeben
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'admin.geoids.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem',
    'Geo IDs administrieren', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'admin.geoids.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
