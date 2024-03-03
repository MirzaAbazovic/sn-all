insert into GUICOMPONENT (ID, NAME, PARENT, APP_ID, VERSION)
   values (S_GUICOMPONENT_0.nextVal, 'auftragsabgleich.gk', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 1, 0);

-- Verrechtung auf Rolle 'auftrag.bearbeiter'
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
     VALUES (S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
        (SELECT ID FROM GUICOMPONENT WHERE name = 'auftragsabgleich.gk' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame'));

