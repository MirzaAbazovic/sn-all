-- Import von VDSL an HVT
UPDATE GUICOMPONENT SET NAME = 'command.import.strasse.kvz.action', DESCRIPTION = 'Importiert KVZ Straßendaten', TYPE = 'MenuItem' WHERE ID = 1586;

INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'command.import.strasse.vdsl.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem',
    'Importiert Straßendaten für VDSL an HVT', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'command.import.strasse.vdsl.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
