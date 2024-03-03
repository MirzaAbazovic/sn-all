insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
values ( S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.auftrag.innenauftrag.CreateIABudgetDialog', null, null, 1, 0);


INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 37, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.auftrag.innenauftrag.CreateIABudgetDialog';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 176, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.auftrag.innenauftrag.CreateIABudgetDialog';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 43, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.auftrag.innenauftrag.CreateIABudgetDialog';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 27, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.auftrag.innenauftrag.CreateIABudgetDialog';
