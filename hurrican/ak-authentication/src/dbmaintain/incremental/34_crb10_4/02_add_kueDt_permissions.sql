-- Verrechtung der Buttons auf "Kuendigung DTAG" Panel

-- Die Rolle 'auftrag.bearbeiter'
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'save.kue.dt.user.task', 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'save.kue.dt.user.task' AND parent = 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'abschliessen', 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'abschliessen' AND parent = 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'detail', 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'detail' AND parent = 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'task.bearbeiten', 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'task.bearbeiten' AND parent = 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel';

-- Die Rolle 'wita.superuser'
INSERT into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'task.freigeben', 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, (select ID FROM ROLE WHERE name='wita.superuser'), '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'task.freigeben' AND parent = 'de.augustakom.hurrican.gui.tools.tal.KuendigungDtPanel';
