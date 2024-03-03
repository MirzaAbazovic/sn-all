-- Nur der Superuser kann den Projektkenner fuer TAL setzen --

INSERT into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'projektkenner', 'de.augustakom.hurrican.gui.tools.tal.wizard.CBVorgangDetailWizardPanel',
  'TextArea', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, (select ID FROM ROLE WHERE name='wita.superuser'), '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'projektkenner' AND parent = 'de.augustakom.hurrican.gui.tools.tal.wizard.CBVorgangDetailWizardPanel';
    
INSERT into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'kopplungkenner', 'de.augustakom.hurrican.gui.tools.tal.wizard.CBVorgangDetailWizardPanel',
  'TextArea', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, (select ID FROM ROLE WHERE name='wita.superuser'), '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'kopplungkenner' AND parent = 'de.augustakom.hurrican.gui.tools.tal.wizard.CBVorgangDetailWizardPanel';
    
    