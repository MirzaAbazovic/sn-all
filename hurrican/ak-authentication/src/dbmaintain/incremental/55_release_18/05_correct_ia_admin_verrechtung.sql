DELETE FROM COMPBEHAVIOR
where COMP_ID IN (SELECT id
    FROM GUICOMPONENT
    WHERE name = 'admin.innenauftrag.action' AND type = 'MenuItem')
and ROLE_ID IN (SELECT id
    FROM ROLE
    WHERE name = 'innenauftrag.admin')
;

DELETE FROM GUICOMPONENT where name = 'admin.innenauftrag.action';

INSERT INTO GUICOMPONENT
(ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
VALUES
  (S_GUICOMPONENT_0.nextVal, 'admin.ia.levels.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
   'MenuItem', 'MenuItem fuer die Administration der Innenauftrags-Ebenen.', 1, 0);

INSERT INTO COMPBEHAVIOR
(ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
VALUES
  (S_COMPBEHAVIOR_0.nextVal,
   (SELECT id
    FROM GUICOMPONENT
    WHERE name = 'admin.ia.levels.action' AND type = 'MenuItem'),
   (SELECT id
    FROM ROLE
    WHERE name = 'innenauftrag.admin'), '1', '1', 0);
