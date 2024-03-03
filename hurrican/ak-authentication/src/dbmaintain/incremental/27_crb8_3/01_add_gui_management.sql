
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'add.data', 'de.augustakom.hurrican.gui.auftrag.AuftragSIPPanel', 'BUTTON',
    1, 0);
Insert into GUICOMPONENT
   (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.auftrag.EditSipInterTrunkDialog', 'BUTTON',
    1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 151, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'addData' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragSIPPanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 25, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'addData' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragSIPPanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 27, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'addData' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragSIPPanel';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 151, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.auftrag.EditSipInterTrunkDialog';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 25, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.auftrag.EditSipInterTrunkDialog';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 27, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.auftrag.EditSipInterTrunkDialog';

