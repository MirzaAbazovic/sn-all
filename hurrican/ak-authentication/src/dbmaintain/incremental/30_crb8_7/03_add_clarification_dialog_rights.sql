INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.shared.GeoId2TechLocationEditDialog', 'Button',
    'Save-Button in Edit-Dialog fuer GeoID Klaerungen', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.shared.GeoId2TechLocationEditDialog';

