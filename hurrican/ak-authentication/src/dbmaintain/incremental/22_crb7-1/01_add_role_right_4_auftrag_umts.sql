insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
values ( S_GUICOMPONENT_0.nextVal, 'save.auftrag.umts.panel', 'de.augustakom.hurrican.gui.auftrag.AuftragDataFrame', 'Button', 'Button (Save), um die Auftrags-UMTS Daten zu speichern.', 1, 0);

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
values ( S_GUICOMPONENT_0.nextVal, 'addData', 'de.augustakom.hurrican.gui.auftrag.AuftragUMTSPanel', 'Button', 'Button, um UMTS Daten für Auftrag anzulegen.', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 316, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save.auftrag.umts.panel' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragDataFrame';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 30, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save.auftrag.umts.panel' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragDataFrame';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 316, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'addData' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragUMTSPanel';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 30, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'addData' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragUMTSPanel';
