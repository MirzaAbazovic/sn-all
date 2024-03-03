
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'positive.ruempv', 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'positive.ruempv' AND parent = 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'negative.ruempv', 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'negative.ruempv' AND parent = 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel';