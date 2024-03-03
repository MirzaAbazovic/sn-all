
INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'action.edit.tech.type', 'de.augustakom.hurrican.gui.hvt.HVTTechTypeAdminPanel', 'MenuItem',
    'Technologietypen bearbeiten', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'action.edit.tech.type' AND parent = 'de.augustakom.hurrican.gui.hvt.HVTTechTypeAdminPanel';

INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'action.delete.tech.type', 'de.augustakom.hurrican.gui.hvt.HVTTechTypeAdminPanel', 'MenuItem',
    'Technologietypen löschen', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'action.delete.tech.type' AND parent = 'de.augustakom.hurrican.gui.hvt.HVTTechTypeAdminPanel';

INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'btn.delete.tech.type', 'de.augustakom.hurrican.gui.hvt.HVTTechTypeAdminPanel', 'Button',
    'Technologietypen löschen', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 15, '1', '1', ID
    FROM GUICOMPONENT
    WHERE name = 'btn.delete.tech.type' AND parent = 'de.augustakom.hurrican.gui.hvt.HVTTechTypeAdminPanel';

