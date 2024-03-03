-- Rechte für Hurrican einfügen
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'rangierung.entfernen', 'de.augustakom.hurrican.gui.auftrag.RangierungPanel',
    'Button', 'Button, um aktuelle Rangierungen zu entfernen', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('rangierung.entfernen')
        AND g.PARENT = 'de.augustakom.hurrican.gui.auftrag.RangierungPanel'
        AND r.NAME = 'HURRICAN_AM_BEARBEITER' AND r.APP_ID = 1;
