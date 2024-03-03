INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    VALUES (S_GUICOMPONENT_0.nextVal,
    'its.tools', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'SubMenu', 'SubMenu fuer Tools\nder Abteilung ITS', 1);

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    VALUES (S_GUICOMPONENT_0.nextVal,
    'its.remove.rangierung.action', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'MenuItem', 'MenuItem für das\ngezielte Entfernen\nvon Rangierungen.', 1);
    

INSERT INTO ROLE (ID, NAME, DESCRIPTION, APP_ID, IS_ADMIN, IS_PARENT)
    VALUES (s_role_0.nextval,
    'rangierung.entfernen', 'Rolle für besondere AM-Mitarbeiter.<br>Diese können fehlerhaft gezogene Rangierungen wieder entfernen',
    1, '0', NULL);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
  FROM GUICOMPONENT g, ROLE r
  WHERE g.name='its.tools'
  AND g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame'
  AND r.name='rangierung.entfernen';

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
  FROM GUICOMPONENT g, ROLE r
  WHERE g.name='its.remove.rangierung.action'
  AND g.parent='de.augustakom.hurrican.gui.system.HurricanMainFrame'
  AND r.name='rangierung.entfernen';

