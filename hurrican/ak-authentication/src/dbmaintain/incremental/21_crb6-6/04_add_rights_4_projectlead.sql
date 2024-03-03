INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'projektleiter', 'de.augustakom.hurrican.gui.auftrag.AuftragStammdatenPanel',
    null, null, 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
FROM GUICOMPONENT g, ROLE r
WHERE g.NAME IN ('projektleiter')
    AND g.PARENT = 'de.augustakom.hurrican.gui.auftrag.AuftragStammdatenPanel'
    AND r.NAME IN ('verlauf.dispo', 'am.projekte', 'zentrale.dispo') AND r.APP_ID = 1;
