-- Verhalten der GUI Komponenten definieren (korrekt)
-- cps.worker Rollen definieren
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 150, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'button.state'
            AND parent = 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 150, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'button.cancel'
            AND parent = 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 150, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'button.resend'
            AND parent = 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 150, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'button.close'
            AND parent = 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel';
