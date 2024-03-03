INSERT INTO GUICOMPONENT
   (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
 VALUES
   (S_GUICOMPONENT_0.nextVal, 'switch.migration.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
   'MenuItem', 'MenuItem für die Switch Migration.', 1, 0);
INSERT INTO COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 VALUES
   (S_COMPBEHAVIOR_0.nextVal, (select ID from GUICOMPONENT where name = 'switch.migration.action'), 43, '1', '1', 0);
