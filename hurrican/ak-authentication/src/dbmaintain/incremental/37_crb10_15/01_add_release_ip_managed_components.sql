Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'ip.deallocate.menue', 'de.augustakom.hurrican.gui.auftrag.internet.IPPanel',
    'MenuItem', 'IP-Addresse in monline über Kontextmenü freigeben', 1, 0);
    
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'deallocate.ip', 'de.augustakom.hurrican.gui.auftrag.internet.InternetPanel',
    'BUTTON', 'IP-Addresse in monline über Button freigeben', 1, 0);
    
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'ip.deallocate.menue'
            AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.IPPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='assign.ip.ripe');

INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'deallocate.ip'
            AND parent = 'de.augustakom.hurrican.gui.auftrag.internet.InternetPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='assign.ip.ripe');