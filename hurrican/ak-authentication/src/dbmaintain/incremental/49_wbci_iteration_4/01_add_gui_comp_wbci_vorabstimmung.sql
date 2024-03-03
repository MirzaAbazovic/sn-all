-- script to add the wbci menu to the gui --
-- add the menu -
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'wbci.tools', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MENU', 'Menu for WBCI Frame', 1, 0);
-- add the menu item --
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'wbci.vorabstimmung.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MENUITEM', 'Vorabstimungs-Menu for WBCI', 1, 0);

--SELECT GUICOMPONENT.* FROM GUICOMPONENT WHERE NAME like '%wbci%';