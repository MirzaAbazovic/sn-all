INSERT INTO ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, VERSION)
VALUES (S_ROLE_0.nextVal, 'innenauftrag.admin', 'Rolle um die Innenauftrags-Ebenen zu pflegen', 1, '0', 0);

INSERT INTO GUICOMPONENT
(ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
VALUES
  (S_GUICOMPONENT_0.nextVal, 'admin.innenauftrag.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
   'MenuItem', 'MenuItem fuer die Administration der Innenauftrags-Ebenen.', 1, 0);

INSERT INTO COMPBEHAVIOR
(ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
VALUES
  (S_COMPBEHAVIOR_0.nextVal,
   (SELECT id
    FROM GUICOMPONENT
    WHERE name = 'admin.innenauftrag.action' AND type = 'MenuItem'),
   (SELECT id
    FROM ROLE
    WHERE name = 'innenauftrag.admin'), '1', '1', 0);
