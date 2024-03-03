INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('connect.gebaeude', 'connect.etage', 'connect.raum', 'connect.schrank'
            , 'connect.uebergabe', 'connect.bandbreite', 'connect.if', 'connect.if-einstellung'
            , 'connect.routerinfo', 'connect.routertyp', 'connect.bemerkung')
        AND g.PARENT = 'de.augustakom.hurrican.gui.auftrag.EndstelleConnectPanel'
        AND r.NAME = 'am.projekte';
