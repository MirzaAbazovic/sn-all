INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'ba.zentraledispo.erstellen', 'de.augustakom.hurrican.gui.system.HurricanMainFrame',
    'MenuItem', '', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('ba.zentraledispo.erstellen')
    AND g.PARENT = 'de.augustakom.hurrican.gui.system.HurricanMainFrame'
    AND r.NAME IN ('am.projekte', 'auftrag.bearbeiter.pmx') AND r.APP_ID = 1;
