-- Verrechtung des Tree Views der Innenauftraege
-- Bitte beachten: Daten sind teilweise manuell auf Test bereits konfiguriert -> 'weiche' Inserts verwenden!

-- -----------------------------
--  Popup Menu 'Unterebene oeffnen'
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    SELECT S_GUICOMPONENT_0.nextVal, 'expand.children', 'de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame',
    'Popup-MenuItem', 'Unterebene oeffnen', 1, 0 FROM dual WHERE NOT EXISTS (SELECT * FROM GUICOMPONENT
    WHERE name = 'expand.children' AND parent = 'de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='expand.children'
      AND g.parent='de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='expand.children'
    AND g.parent='de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame'));

-- -----------------------------
--  Popup Menu 'Neue Ebene anlegen'
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    SELECT S_GUICOMPONENT_0.nextVal, 'add.new.level', 'de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame',
    'Popup-MenuItem', 'Neue Ebene anlegen', 1, 0 FROM dual WHERE NOT EXISTS (SELECT * FROM GUICOMPONENT
    WHERE name = 'add.new.level' AND parent = 'de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='add.new.level'
      AND g.parent='de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='add.new.level'
    AND g.parent='de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame'));

-- -----------------------------
--  Popup Menu 'Ebene editieren'
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
    SELECT S_GUICOMPONENT_0.nextVal, 'edit.level', 'de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame',
    'Popup-MenuItem', 'Ebene editieren', 1, 0 FROM dual WHERE NOT EXISTS (SELECT * FROM GUICOMPONENT
    WHERE name = 'edit.level' AND parent = 'de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='edit.level'
      AND g.parent='de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='edit.level'
    AND g.parent='de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame'));

-- -----------------------------
--  Popup Menu 'Ebene loeschen'
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  SELECT S_GUICOMPONENT_0.nextVal, 'delete.level', 'de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame',
    'Popup-MenuItem', 'Ebene loeschen', 1, 0 FROM dual WHERE NOT EXISTS (SELECT * FROM GUICOMPONENT
  WHERE name = 'delete.level' AND parent = 'de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='delete.level'
      AND g.parent='de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='delete.level'
    AND g.parent='de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame'));

-- -----------------------------
--  Button 'Ebene speichern'
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  SELECT S_GUICOMPONENT_0.nextVal, 'save', 'de.augustakom.hurrican.gui.innenauftrag.EditIaLevelDialog',
    'Button', 'Ebene speichern/aktualisieren', 1, 0 FROM dual WHERE NOT EXISTS (SELECT * FROM GUICOMPONENT
  WHERE name = 'save' AND parent = 'de.augustakom.hurrican.gui.innenauftrag.EditIaLevelDialog');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='save'
      AND g.parent='de.augustakom.hurrican.gui.innenauftrag.EditIaLevelDialog') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'innenauftrag.admin')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='save'
    AND g.parent='de.augustakom.hurrican.gui.innenauftrag.EditIaLevelDialog'));
