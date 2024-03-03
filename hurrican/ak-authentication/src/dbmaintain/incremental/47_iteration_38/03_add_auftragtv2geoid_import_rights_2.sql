INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
     VALUES (S_COMPBEHAVIOR_0.nextVal, 
        (SELECT ID FROM ROLE WHERE NAME = 'wowi.import'), 
        '1', '1', 
        (SELECT ID FROM GUICOMPONENT WHERE name = 'import.tools' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame'));
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'import.tools' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';