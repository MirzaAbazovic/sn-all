-- Buttons auf "offene TAMs" Panel fuer Rolle 'auftrag.bearbeiter'
Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'save.tam.user.task', 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'save.tam.user.task' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, '60tage', 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = '60tage' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'storno', 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'storno' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'tv', 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'tv' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'erlmk', 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'erlmk' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel';

Insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'task.bearbeiten', 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, 9, '1', '1', ID
    FROM GUICOMPONENT WHERE name = 'task.bearbeiten' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel';



-- Freigabe-Button fuer neue Rolle (wita.superuser)!!!
INSERT into ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, VERSION)
  VALUES (S_ROLE_0.nextVal, 'wita.superuser', 'Rolle fuer WITA SuperUser.', 1, '0', 0);
INSERT into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'task.freigeben', 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel', 'Button', 1, 0);
INSERT INTO COMPBEHAVIOR (ID, VISIBLE, EXECUTABLE, COMP_ID, ROLE_ID)
    SELECT S_COMPBEHAVIOR_0.nextVal, '1', '1', COMP_ID_TMP, ROLE_ID_TMP
      FROM
        (select ID as COMP_ID_TMP from GUICOMPONENT WHERE name = 'task.freigeben' AND parent = 'de.augustakom.hurrican.gui.tools.tal.OffeneTamsPanel'),
        (select ID as ROLE_ID_TMP FROM ROLE WHERE name='wita.superuser');

INSERT INTO USERROLE (ID, USER_ID, ROLE_ID)
  SELECT S_USERROLE_0.nextVal, USER_ID_TMP, ROLE_ID_TMP
    FROM
      (select ID as USER_ID_TMP from USERS WHERE NAME='Seyfarth'),
      (select ID as ROLE_ID_TMP from ROLE WHERE name='wita.superuser');

