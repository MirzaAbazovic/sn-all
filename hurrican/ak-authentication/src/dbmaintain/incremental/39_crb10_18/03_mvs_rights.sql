-- GUI-Berechtigungen fuer MVS Panel
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'save.auftrag.mvs.enterprise.panel', 'de.augustakom.hurrican.gui.auftrag.AuftragDataFrame', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'save.auftrag.mvs.enterprise.panel' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragDataFrame';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'new', 'de.augustakom.hurrican.gui.auftrag.mvs.AuftragMVSEnterprisePanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'new' AND parent = 'de.augustakom.hurrican.gui.auftrag.mvs.AuftragMVSEnterprisePanel';

