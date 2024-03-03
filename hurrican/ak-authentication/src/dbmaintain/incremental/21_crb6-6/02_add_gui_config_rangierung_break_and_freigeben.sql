INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'rangierungen.freigeben', 'de.augustakom.hurrican.gui.base.tree.hardware.EditRangierungPanel',
    'Button', 'Button zum Freigeben der marktierten Rangierungen', 1, 0);

INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'rangierungen.aufbrechen', 'de.augustakom.hurrican.gui.base.tree.hardware.EditRangierungPanel',
    'Button', 'Button zum Aufbrechen der markierten Rangierungen', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('rangierungen.freigeben')
        AND g.PARENT = 'de.augustakom.hurrican.gui.base.tree.hardware.EditRangierungPanel'
        AND r.NAME IN ('admin.rangierung') AND r.APP_ID = 1;


INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('rangierungen.aufbrechen.freigeben')
        AND g.PARENT = 'de.augustakom.hurrican.gui.base.tree.hardware.EditRangierungPanel'
        AND r.NAME IN ('admin.rangierung') AND r.APP_ID = 1;
