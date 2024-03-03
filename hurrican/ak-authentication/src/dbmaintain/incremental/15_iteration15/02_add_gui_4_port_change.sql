INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION) VALUES
    (S_GUICOMPONENT_0.NEXTVAL, 'save', 'de.augustakom.hurrican.gui.hvt.hardware.CreateHWBaugruppenChangeDialog',
    'Button', 'Button, um eine neue Planung zu speichern', 1, 0);

INSERT INTO COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
    SELECT S_COMPBEHAVIOR_0.NEXTVAL, r.ID, '1', '1', g.ID
    FROM GUICOMPONENT g, ROLE r
    WHERE g.NAME IN ('save')
        AND g.PARENT = 'de.augustakom.hurrican.gui.hvt.hardware.CreateHWBaugruppenChangeDialog'
        AND r.NAME = 'physik.master' AND r.APP_ID = 1;
