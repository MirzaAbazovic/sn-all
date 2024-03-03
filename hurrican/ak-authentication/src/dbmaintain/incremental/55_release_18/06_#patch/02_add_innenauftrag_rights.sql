-- Verrechtung der Innenauftraege
-- Bitte beachten: Daten sind teilweise manuell auf Test bereits konfiguriert -> 'weiche' Inserts verwenden!
-- Top Menu 'Administration' zusaetzliches Recht zuweisen
INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin' AND r.app_id = 1), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='menu.administration'
      AND g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame' AND g.type = 'Menu') FROM dual
  WHERE NOT EXISTS (SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin' AND r.app_id = 1)
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='menu.administration'
    AND g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame' AND g.type = 'Menu'));


-- DB Account cc.reader entfernen
DELETE FROM USERACCOUNT WHERE USER_ID = (SELECT ID FROM USERS WHERE lower(LOGINNAME) = 'majdandzicto')
      AND ACCOUNT_ID = (SELECT ID FROM ACCOUNT WHERE ACC_NAME = 'cc.reader')
      AND DB_ID = (SELECT ID FROM DB WHERE NAME = 'cc');


-- DB Account cc.writer hinzufuegen
INSERT INTO USERACCOUNT (ID, USER_ID, ACCOUNT_ID, DB_ID, VERSION)
  SELECT S_USERACCOUNT_0.nextval, (SELECT ID FROM USERS WHERE lower(LOGINNAME) = 'majdandzicto'),
    (SELECT ID FROM ACCOUNT WHERE ACC_NAME = 'cc.writer'), (SELECT ID FROM DB WHERE NAME = 'cc'), 0 FROM dual
  WHERE NOT EXISTS (SELECT * FROM USERACCOUNT WHERE USER_ID = (SELECT ID FROM USERS WHERE lower(LOGINNAME) = 'majdandzicto')
    AND ACCOUNT_ID = (SELECT ID FROM ACCOUNT WHERE ACC_NAME = 'cc.writer') AND DB_ID = (SELECT ID FROM DB WHERE NAME = 'cc'));
