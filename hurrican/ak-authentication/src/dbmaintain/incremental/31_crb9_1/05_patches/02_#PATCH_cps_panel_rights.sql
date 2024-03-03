-- Verrechtung der Buttons im CPSTxDetailPanel/CPSTxButtonPanel
-- GUI Komponenten deklarieren
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, DESCRIPTION, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'button.state', 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel',
    1, 'Zeigt den Status der CPS-Transaktion', 0);
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, DESCRIPTION, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'button.cancel', 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel',
    1, 'Bricht die CPS-Transaktion ab', 0);
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, DESCRIPTION, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'button.resend', 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel',
    1, 'Sendet die CPS-Transaktion erneut', 0);
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, DESCRIPTION, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'button.close', 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel',
    1, 'Schlieﬂt die CPS-Transaktion', 0);

-- Verhalten der GUI Komponenten definieren
-- cps.worker Rollen definieren
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 150, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'button.state'
            AND parent = 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel.CPSTxButtonPanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 150, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'button.cancel'
            AND parent = 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel.CPSTxButtonPanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 150, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'button.resend'
            AND parent = 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel.CPSTxButtonPanel';
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 150, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'button.close'
            AND parent = 'de.augustakom.hurrican.gui.cps.CPSTxDetailPanel.CPSTxButtonPanel';
-- sperre.ersteller Rolle definieren
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 18, '1', '1', ID
        FROM GUICOMPONENT
        WHERE name = 'cps.transaction.action'
            AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
