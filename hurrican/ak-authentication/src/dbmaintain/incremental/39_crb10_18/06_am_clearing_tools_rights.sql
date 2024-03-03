-- GUI-Berechtigungen fuer Wita-Clearing-Tools Panel
INSERT into GUICOMPONENT (ID, NAME, PARENT, TYPE, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'wita.clearing.tools.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame', 'MenuItem', 1, 0);
INSERT into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextval,  (select id from ROLE where name = 'wita.clearing'), '1', '1', ID 
    FROM GUICOMPONENT WHERE name = 'wita.clearing.tools.action' AND parent = 'de.augustakom.hurrican.gui.system.HurricanMainFrame';
    
DELETE FROM USERROLE
      WHERE USER_ID = (SELECT ID AS USER_ID_TMP
      FROM USERS WHERE LOGINNAME = 'HimmelCh')
            AND role_id = (SELECT ID AS ROLE_ID_TMP
            FROM ROLE WHERE name = 'wita.clearing');

