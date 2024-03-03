-- 'Administration/Geo IDs.../Kl�rungen' Komponenten f�r Rolle 'admin.strassen' freigeben
-- New
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'btn.change.state', 'de.augustakom.hurrican.gui.geoid.GeoIdClarificationPanel', 'Button',
    'Kl�rung neuen Status zuweisen', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'btn.change.state' AND parent = 'de.augustakom.hurrican.gui.geoid.GeoIdClarificationPanel';

-- Edit
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'cb.change.states', 'de.augustakom.hurrican.gui.geoid.GeoIdClarificationPanel', 'ComboBox',
    'Stati die der selektierten Kl�rung zugewiesen werden d�rfen', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'cb.change.states' AND parent = 'de.augustakom.hurrican.gui.geoid.GeoIdClarificationPanel';
