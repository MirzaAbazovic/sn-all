INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'force.address.sync', 'de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel',
    'MenuItem', 'MenuItem, um einen Adress-Sync Taifun>>Hurrican auszufuehren', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('force.address.sync')
        AND g.PARENT = 'de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel'
        AND r.NAME = 'auftrag.bearbeiter' AND r.APP_ID = 1;




