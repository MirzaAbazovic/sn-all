INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'save', 'de.augustakom.hurrican.gui.auftrag.ChangeLayer2ProtocolDialog',
    'Button', 'Button um das Schicht2-Protokoll f�r Ports zu �ndern', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('save')
        AND g.PARENT = 'de.augustakom.hurrican.gui.auftrag.ChangeLayer2ProtocolDialog'
        AND r.NAME IN ('verlauf.stonline', 'verlauf.stconnect') AND r.APP_ID = 1;
