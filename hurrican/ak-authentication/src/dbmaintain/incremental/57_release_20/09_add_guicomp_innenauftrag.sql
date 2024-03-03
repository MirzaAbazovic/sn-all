-- Speicher Button Innenaufträge verrechten

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  SELECT S_GUICOMPONENT_0.nextVal, 'save.innenauftrag.panel', 'de.augustakom.hurrican.gui.auftrag.AuftragDataFrame',
    'Button', 'Innenauftrag speichern/aktualisieren', 1, 0 FROM dual WHERE NOT EXISTS (SELECT * FROM GUICOMPONENT
  WHERE name = 'save.innenauftrag.panel' AND parent = 'de.augustakom.hurrican.gui.auftrag.AuftragDataFrame');

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, (SELECT r.id FROM ROLE r WHERE r.name = 'budget.bearbeiter'), '1', '1',
    (SELECT g.ID FROM GUICOMPONENT g WHERE g.name='save.innenauftrag.panel'
      AND g.parent='de.augustakom.hurrican.gui.auftrag.AuftragDataFrame') FROM dual
  WHERE NOT EXISTS(SELECT * FROM COMPBEHAVIOR WHERE ROLE_ID = (SELECT r.id FROM ROLE r WHERE r.name = 'budget.bearbeiter')
    AND COMP_ID = (SELECT g.id FROM GUICOMPONENT g WHERE g.name='save.innenauftrag.panel'
    AND g.parent='de.augustakom.hurrican.gui.auftrag.AuftragDataFrame'));
