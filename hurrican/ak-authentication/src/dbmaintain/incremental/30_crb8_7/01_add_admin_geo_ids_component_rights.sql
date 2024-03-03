-- 'Administration/Geo IDs...' Buttons, Popup Menü Items für Rolle 'admin.strassen' freigeben
-- New
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'btn.new', 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel', 'Button',
    'Neuen Standort einer Geo ID zuordnen', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'btn.new' AND parent = 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel';

-- Edit
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'btn.edit', 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel', 'Button',
    'Standort einer Geo ID bearbeiten', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'btn.edit' AND parent = 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel';

-- Delete
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'btn.delete', 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel', 'Button',
    'Standort einer Geo ID entfernen', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'btn.delete' AND parent = 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel';

-- Edit Location Action
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'edit.location.action', 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel', 'MenuItem',
    'Popup MenuItem zum bearbeiten eines Standortes', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'edit.location.action' AND parent = 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel';

-- Delete Location Action
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'delete.location.action', 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel', 'MenuItem',
    'Popup MenuItem zum entfernen eines Standortes', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'delete.location.action' AND parent = 'de.augustakom.hurrican.gui.geoid.GeoIdAdminPanel';

-- Search Geo ID Action
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'open.geoid.search.frame.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'TButton,MenuItem', 'Toolbar-Button und MenuItem, um die Geo ID Suchmaske zu öffnen.', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 8, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'open.geoid.search.frame.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
