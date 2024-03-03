-- 'Standort bearbeiten' Button in Endstellen Maske im Auftrags Frame (Auftragsdaten für Auftrag)
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'btn.edit.location', 'de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel', 'Button',
    'Standortzuordnung bearbeiten', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 496, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'btn.edit.location' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel';
