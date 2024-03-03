
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    VALUES (S_GUICOMPONENT_0.nextVal,
    'mb.start', 'de.augustakom.hurrican.gui.base.tree.hardware.MassenbenachrichtigungPanel',
    'Button', 'Button um die\nMassenbenachrichtigung\nanzustoﬂen', 1);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  SELECT S_COMPBEHAVIOR_0.nextVal, r.id, '1', '1', g.ID
  FROM GUICOMPONENT g, ROLE r
  WHERE g.name='mb.start'
  AND g.parent='de.augustakom.hurrican.gui.base.tree.hardware.MassenbenachrichtigungPanel'
  AND r.name='kubena';
