Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'open.transponder.groups.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'MenuItem', 'Admin-Frame fuer Transponder-Gruppen oeffnen', 1, 0);
    

INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'open.transponder.groups.action'
            AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='zentrale.dispo');

        
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.customer.TransponderDialog',
    'BUTTON', 'Transponder speichern', 1, 0);
    

INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'save'
            AND parent = 'de.augustakom.hurrican.gui.customer.TransponderDialog'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='zentrale.dispo');
